package uk.org.taverna.scufl2.api.container;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

/**
 * @author Alan R Williams
 * 
 */
public class WorkflowBundle extends AbstractNamed implements WorkflowBean,
		Named, Root {

	public static final URI WORKFLOW_BUNDLE_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflowBundle/");

	public static URI generateIdentifier() {
		return WORKFLOW_BUNDLE_ROOT.resolve(UUID.randomUUID().toString() + "/");
	}

	private NamedSet<Annotation> annotations = new NamedSet<Annotation>();
	private URI globalBaseURI = generateIdentifier();
	private final NamedSet<Profile> profiles = new NamedSet<Profile>();
	private final NamedSet<Workflow> workflows = new NamedSet<Workflow>();
	private Workflow mainWorkflow;
	private Profile mainProfile;
	private UCFPackage resources;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getWorkflows());
			children.add(getProfiles());
			children.add(getAnnotations());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}

	public Profile getMainProfile() {
		return mainProfile;
	}

	public Workflow getMainWorkflow() {
		return mainWorkflow;
	}

	public NamedSet<Profile> getProfiles() {
		return profiles;
	}

	public UCFPackage getResources() {
		if (resources == null) {
			try {
				resources = new UCFPackage();
			} catch (IOException e) {
				throw new IllegalStateException(
						"Can't create new UCF package, no access to tmpdir?", e);
			}
		}
		return resources;
	}

	@Override
	public URI getGlobalBaseURI() {
		return globalBaseURI;
	}

	public NamedSet<Workflow> getWorkflows() {
		return workflows;
	}

	public void setMainProfile(Profile mainProfile) {
		if (mainProfile != null) {
			getProfiles().add(mainProfile);
		}
		this.mainProfile = mainProfile;
	}

	public void setMainWorkflow(Workflow mainWorkflow) {
		if (mainWorkflow != null) {
			getWorkflows().add(mainWorkflow);
		}
		this.mainWorkflow = mainWorkflow;
	}

	public void setProfiles(Set<Profile> profiles) {
		this.profiles.clear();
		this.profiles.addAll(profiles);
	}

	public void setResources(UCFPackage resources) {
		this.resources = resources;
	}

	/**
	 * Return the folder for annotation resources.
	 * <p>
	 * This folder name can be used with
	 * getResources().listResources(folderPath) to retrieve the annotation
	 * resources, or used with getResources().addResource(..) for adding
	 * annotation resources.
	 * <p>
	 * The annotation folder is normally fixed to be <code>"annotations/"</code>.
	 * 
	 * @return Folder name for annotations
	 */
	public String getAnnotationResourcesFolder() {
		return "annotations/";
	}
	

	@Override
	public void setGlobalBaseURI(URI globalBaseURI) {
		this.globalBaseURI = globalBaseURI;
	}

	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows.clear();
		this.workflows.addAll(workflows);
	}

	/**
	 * WorkflowBundles are only equal by instance identity.
	 * <p>
	 * Thus, if you load or construct the same workflow bundle twice, say as
	 * <code>wb1</code> and <code>wb2</code>, then
	 * <code>wb1.equals(wb2) == false</code>.
	 * <p>
	 * There are two reasons for this. Firstly, a workflow bundle is a complex
	 * object, as it bundles not just the {@link #getWorkflows()} and
	 * {@link #getProfiles()}, but also arbitrary resources in
	 * {@link #getResources()}. Two workflow bundles can for most purposes be
	 * assumed "equal" if they have the same identifier in
	 * {@link #getGlobalBaseURI()} - they might however vary in which
	 * annotations they carry.
	 * <p>
	 * The second is that applications might use {@link WorkflowBundle}
	 * instances as keys in a {@link Map} of open workflows, and want to
	 * distinguish between two workflow bundles from two different (but possibly
	 * identical) files; for instance a .t2flow and a .wfbundle.
	 * <p>
	 * Note that contained workflow beans such as {@link Workflow} and
	 * {@link Activity} will likewise not be
	 * {@link AbstractNamed#equals(Object)} across workflow bundles, as a named
	 * bean is considered equal only if its name matches and its parents are
	 * (recursively) equal. You may however detach the children by setting their
	 * parents to <code>null</code> and check for equality in isolation.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public int hashCode() {
		// TODO: Is there a way to call Object.hashCode() from here?
		// Our super is hiding it.

		// Possible workaround:
		/*
		 * private int hashCode = new Object().hashCode();
		 * 
		 * @Override public int hashCode() { return hashCode; }
		 */

		return super.hashCode();
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "TavernaResearchObject [" + "profiles="
				+ (profiles != null ? toString(profiles, maxLen) : null)
				+ ", mainWorkflow=" + mainWorkflow + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public NamedSet<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(NamedSet<Annotation> annotations) {
		this.annotations.clear();
		this.annotations.addAll(annotations);
	}

}
