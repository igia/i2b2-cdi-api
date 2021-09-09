/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/

package io.igia.i2b2.cdi.common.integration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.opencsv.CSVWriter;

import io.igia.i2b2.cdi.derivedconcept.service.DerivedConceptServiceImpl;

@Configuration
@Component
public class SftpOutboundMessageHandler {

    public static final Logger logger = LoggerFactory.getLogger(DerivedConceptServiceImpl.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    AppIntegrationProperties integrationProperties;

    public boolean sendFile(String fileName, String localDirPath) {
	boolean sendFile = false;

	File file = createZipFile(fileName, localDirPath);

	Message<File> message = MessageBuilder.withPayload(file).build();
	sendFile = sftpOutboundChannel().send(message);

	return sendFile;
    }

    private File createZipFile(String fileName, String localDirPath) {
	String projectIdDir = localDirPath + File.separator + fileName;
	String sourceSystemDir = projectIdDir + File.separator + "EDW" + File.separator;
	File dir = new File(sourceSystemDir);
	dir.mkdirs();

	Map<String, Boolean> fileMap = DataFileName.getDataFileMap();
	for (Map.Entry<String, Boolean> entry : fileMap.entrySet()) {
	    if (!entry.getValue()) {
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(sourceSystemDir + entry.getKey()))) {
		    /**
		     * This is empty because, we are creating missing (empty) files
		     */
		} catch (IOException e) {
		    logger.error("Error while creating missing files. {} ", e);
		}
	    }
	}
	return toZipFolder(projectIdDir);
    }

    public static File toZipFolder(String sourceFolderPath) {

	File destZipDir = new File(sourceFolderPath + ".zip");
	try {
	    ZipUtil.pack(new File(sourceFolderPath), destZipDir, true);
	} catch (Exception e) {
	    logger.error("Error while packing as zip file {}", e);
	}
	return destZipDir;
    }

    @Bean
    public MessageChannel sftpOutboundChannel() {
	return new DirectChannel();
    }

    @Bean
    public SessionFactory<LsEntry> sftpSessionFactory() {
	DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
	factory.setHost(integrationProperties.getHost());
	factory.setPort(integrationProperties.getPort());
	factory.setUser(integrationProperties.getUser());
	factory.setPassword(integrationProperties.getPassword());
	factory.setAllowUnknownKeys(true);
	return new CachingSessionFactory<>(factory);
    }

    @Bean
    public IntegrationFlow sftpOutboundFlow() {
	return IntegrationFlows.from("sftpOutboundChannel").handle(Sftp.outboundAdapter(sftpSessionFactory())
		.useTemporaryFileName(true).remoteDirectory(integrationProperties.getRemoteDirPath())).get();
    }
}