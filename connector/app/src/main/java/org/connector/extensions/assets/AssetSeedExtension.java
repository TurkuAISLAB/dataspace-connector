package org.wccsconnector.extensions.assets;

import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;


import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.lang.System;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public class AssetSeedExtension implements ServiceExtension {

    public static final String NAME = "Management API: Asset : Platform Asset Sync";
    
    public String name(){
        return NAME;
    }

    @Setting(value = "The origin of the WCCS Data Platform Dataview Gateway")
    private final String DataPlatformGatewayOriginSetting = "wccs.dataplatform.gateway.dataview.origin";
    
    @Setting(value = "The origin of the WCCS Data Platform Content Delivery Gateway")
    private final String DataPlatformContentGatewayOriginSetting = "wccs.dataplatform.gateway.content.origin";

    @Inject
    private AssetIndex assetIndex;

    @Inject
    private JsonObjectValidatorRegistry validator;

    @Inject
    private TypeTransformerRegistry transformerRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        final String DataPlatformGatewayOrigin = context.getSetting(DataPlatformGatewayOriginSetting, null);
        final String DataPlatformContentGatewayOrigin = context.getSetting(DataPlatformContentGatewayOriginSetting, null);

        try {
            URL url = new URL(DataPlatformGatewayOrigin+"/dataview/json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject blobJson = new JSONObject(content.toString());
            List<Object> jsonFormat = blobJson.getJSONArray("format").toList();
            JSONObject blobData = blobJson.getJSONObject("data");


            blobData.keys().forEachRemaining((key) -> {

                JSONArray blob = blobData.getJSONArray(key);

                int blobIdIndex = jsonFormat.indexOf("blob_id");
                int blobNameIndex = jsonFormat.indexOf("blob_name");
                int contentTypeIndex = jsonFormat.indexOf("content_type");
                int publishTimeIndex = jsonFormat.indexOf("addition_timestamp");
                int titleIndex = jsonFormat.indexOf("title");
                int publisherIndex = jsonFormat.indexOf("publisher");
                int sourceIndex = jsonFormat.indexOf("source");
                int subjectIndex = jsonFormat.indexOf("subject");
                int rightsIndex = jsonFormat.indexOf("rights");
                int minTimestampIndex = jsonFormat.indexOf("min_timestamp");
                int additionTimestampIndex = jsonFormat.indexOf("addition_timestamp");
                int maxTimestampIndex = jsonFormat.indexOf("max_timestamp");

                String blobId = String.format("%d", blob.getInt(blobIdIndex));
                String blobName = blob.getString(blobNameIndex);
                Date minTimestamp = new Date(blob.getLong(minTimestampIndex));
                Date maxTimestamp = new Date(blob.getLong(maxTimestampIndex));
                String title = blob.getString(titleIndex);
                String contentType = blob.getString(contentTypeIndex);
                String publisher = blob.getString(publisherIndex);
                String subject = blob.getString(subjectIndex);
                String source = blob.getString(sourceIndex);
                Date additionDate = new Date(blob.getLong(additionTimestampIndex));
                String rights = blob.getString(rightsIndex);


                String generatedAssetDescription = title +" -> " + minTimestamp + " - " + maxTimestamp;
                
                Map<String, Object> dublinCoreMetadata = new HashMap<>();
                dublinCoreMetadata.put("dct:identifier", "wccs:rdp:blob:"+blobId);
                dublinCoreMetadata.put("dct:date", additionDate.toString());
                dublinCoreMetadata.put("dct:licence", rights);
                dublinCoreMetadata.put("dct:publisher", publisher);
                dublinCoreMetadata.put("dct:subject", subject);
                dublinCoreMetadata.put("dct:source", source);
                dublinCoreMetadata.put("dct:format", contentType);
                dublinCoreMetadata.put("dct:dateSubmitted", additionDate.toString());
                dublinCoreMetadata.put("dct:description", generatedAssetDescription);
                dublinCoreMetadata.put("dct:title", title);

                Asset generatedAsset = Asset.Builder.newInstance()
                    .id(blobId)
                    .name(generatedAssetDescription)
                    .contentType(contentType)
                    .dataAddress(
                        DataAddress.Builder.newInstance()   
                            .type("HttpData")
                            .property("https://w3id.org/edc/v0.0.1/ns/baseUrl", DataPlatformContentGatewayOrigin+"/data/download?blob_id="+blobId)
                            .property("name", blobName)
                            .property("proxyPath", "false")
                            .build()
                    )
                    .properties(dublinCoreMetadata)
                    .property(
                        "@context",
                        Json.createObjectBuilder()
                            .add("@vocab", "https://w3id.org/edc/v0.0.1/ns/")
                            .add("dct", "http://purl.org/dc/terms/")
                            .build()
                    )
                    .build();
                assetIndex.create(generatedAsset);
            });

        } catch(Exception error){
            System.out.println(error);
        }
        
    }  

}
