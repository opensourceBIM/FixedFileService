package org.bimserver.fixedfileservice;

import java.io.IOException;
import java.nio.file.Files;

import org.bimserver.bimbots.BimBotContext;
import org.bimserver.bimbots.BimBotsException;
import org.bimserver.bimbots.BimBotsInput;
import org.bimserver.bimbots.BimBotsOutput;
import org.bimserver.models.store.ByteArrayType;
import org.bimserver.models.store.ObjectDefinition;
import org.bimserver.models.store.ParameterDefinition;
import org.bimserver.models.store.PrimitiveDefinition;
import org.bimserver.models.store.PrimitiveEnum;
import org.bimserver.models.store.StoreFactory;
import org.bimserver.models.store.StringType;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.BimBotAbstractService;

public class FixedFileService extends BimBotAbstractService {
	@Override
	public BimBotsOutput runBimBot(BimBotsInput input, BimBotContext bimBotContext, PluginConfiguration pluginConfiguration) throws BimBotsException {
		byte[] bytes = pluginConfiguration.getByteArray("file");
		BimBotsOutput output = new BimBotsOutput(SchemaName.BCF_ZIP_2_0, bytes);
		output.setTitle("FixedFileService");
		output.setContentType(pluginConfiguration.getString("contentType"));
		return output;
	}
	
	@Override
	public ObjectDefinition getSettingsDefinition() {
		ObjectDefinition settingsDefinition = StoreFactory.eINSTANCE.createObjectDefinition();

		PrimitiveDefinition byteArrayType = StoreFactory.eINSTANCE.createPrimitiveDefinition();
		byteArrayType.setType(PrimitiveEnum.BYTE_ARRAY);

		PrimitiveDefinition stringType = StoreFactory.eINSTANCE.createPrimitiveDefinition();
		stringType.setType(PrimitiveEnum.STRING);

		ParameterDefinition contentTypeParameter = StoreFactory.eINSTANCE.createParameterDefinition();
		contentTypeParameter.setName("Content Type");
		contentTypeParameter.setDescription("ContentType to be used for the returned result");
		contentTypeParameter.setRequired(true);
		contentTypeParameter.setIdentifier("contentType");
		contentTypeParameter.setType(stringType);
		
		StringType contentTypeDefaultValue = StoreFactory.eINSTANCE.createStringType();
		contentTypeDefaultValue.setValue("application/zip");
		
		contentTypeParameter.setDefaultValue(contentTypeDefaultValue);
		settingsDefinition.getParameters().add(contentTypeParameter);
		
		ParameterDefinition fileParameter = StoreFactory.eINSTANCE.createParameterDefinition();
		fileParameter.setName("File");
		fileParameter.setDescription("Fixed file to be served as a result of running the service");
		fileParameter.setRequired(true);
		fileParameter.setType(byteArrayType);
		fileParameter.setIdentifier("file");
		
		ByteArrayType defaultValue = StoreFactory.eINSTANCE.createByteArrayType();
		try {
			defaultValue.setValue(Files.readAllBytes(getPluginContext().getRootPath().resolve("default/opmerkingenv1.bcfzip")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileParameter.setDefaultValue(defaultValue);
		settingsDefinition.getParameters().add(fileParameter);
		
		return settingsDefinition;
	}

	@Override
	public String getOutputSchema() {
		return SchemaName.BCF_ZIP_2_0.name();
	}
}
