package org.purl.wf4ever.robundle.manifest;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "uri", "about", "content" })
public class PathAnnotation {
	private List<URI> about = new ArrayList<>();
	private URI content;
	private URI uri;

	public void generateAnnotationId() {
		setUri(URI.create("urn:uuid:" + UUID.randomUUID()));
	}

	@JsonIgnore
	public URI getAbout() {
		if (about.isEmpty()) {
			return null;
		} else {
			return about.get(0);
		}
	}

	@JsonIgnore
	public List<URI> getAboutList() {
		return about;
	}

	@JsonProperty("about")
	public Object getAboutObject() {
		if (about.isEmpty()) {
			return null;
		}
		if (about.size() == 1) {
			return about.get(0);
		} else {
			return about;
		}
	}

	@Deprecated
	public URI getAnnotion() {
		return getUri();
	}

	public URI getContent() {
		return content;
	}

	public URI getUri() {
		return uri;
	}

	private URI relativizePath(Path path) {
		return URI.create("/.ro/").relativize(
				URI.create(path.toUri().getRawPath()));
	}

	public void setAbout(List<URI> about) {
		if (about == null) {
			throw new NullPointerException("about list can't be null");
		}
		this.about = about;
	}

	public void setAbout(Path path) {
		setAbout(relativizePath(path));
	}

	public void setAbout(URI about) {
		this.about.clear();
		if (about != null) {
			this.about.add(about);
		}
	}

	@Deprecated
	public void setAnnotation(URI annotation) {
		setUri(annotation);
	}

	public void setContent(Path path) {
		this.content = relativizePath(path);
	}

	public void setContent(URI content) {
		this.content = content;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "Annotation: " + getContent() + " about " + getAbout();
	}
}