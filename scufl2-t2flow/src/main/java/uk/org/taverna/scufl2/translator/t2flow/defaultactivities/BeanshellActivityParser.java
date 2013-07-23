package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BasicArtifact;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ClassLoaderSharing;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BeanshellActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/beanshell-activity/");

	private static URI localWorkerActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/localworker-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.beanshell.BeanshellActivity";

	private static String localWorkerActivityClassName = "net.sf.taverna.t2.activities.localworker.LocalworkerActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	public static URI LOCAL_WORKER_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/localworker/");

	
	public static URI DEPENDENCY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/dependency");
	
	private static URITools uriTools = new URITools();

	

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName)) {
			return true;
		}
		if (activityUriStr.startsWith(localWorkerActivityRavenURI
				.toASCIIString())
				&& activityUriStr.endsWith(localWorkerActivityClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		
	
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));
		
		if (beanshellConfig.getLocalworkerName() != null) {
			URI localWorkerURI = LOCAL_WORKER_URI.resolve(uriTools.validFilename(beanshellConfig.getLocalworkerName()));
			URI relation = ACTIVITY_URI.resolve("#derivedFrom");
			// FIXME: As we can't read the annotation chain yet, we can't tell
			// whether this local worker has been edited or not, and so 
			// can't use #definedBy
			json.put("derivedFrom", localWorkerURI.toString());
		}
		
		
		String script = beanshellConfig.getScript();
		json.put("script", script);

		ClassLoaderSharing classLoaderSharing = beanshellConfig.getClassLoaderSharing();
		if (classLoaderSharing == ClassLoaderSharing.SYSTEM) {
		    json.put("classLoaderSharing", "system");
		} else {
		    // default is "workflow" but don't need to be expressed
//		    json.put("classLoaderSharing", "workflow");
		}
 		
		if (beanshellConfig.getLocalDependencies() != null) {			
		    ArrayNode dependencies = json.arrayNode();
			for (String localDep : beanshellConfig.getLocalDependencies().getString()) {
			    dependencies.add(localDep);
			}
			if (dependencies.size() > 0) {
			    json.put("localDependency", dependencies);
			}
		}

		/**
		 * Note: Maven Dependencies are not supported by Taverna 3 - 
		 * only here for informational purposes and 
		 * potential t2flow->t2flow scenarios
		 */
		if (beanshellConfig.getArtifactDependencies() != null) {			
		    ArrayNode dependencies = json.arrayNode();
			for (BasicArtifact mavenDep : beanshellConfig.getArtifactDependencies().getNetSfTavernaRavenRepositoryBasicArtifact()) {
			    ObjectNode mavenDependency = json.objectNode();
			    dependencies.add(mavenDependency);
			    mavenDependency.put("groupId", mavenDep.getGroupId());
                mavenDependency.put("artifactId", mavenDep.getArtifactId());
                mavenDependency.put("version", mavenDep.getVersion());
			}
			if (dependencies.size() > 0) {
			    json.put("mavenDependency", dependencies);
			}
		}
		
		Activity activity = parserState.getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		}
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			parseAndAddOutputPortDefinition(portBean, configuration, activity);
			
		}
		return configuration;
	}

}
