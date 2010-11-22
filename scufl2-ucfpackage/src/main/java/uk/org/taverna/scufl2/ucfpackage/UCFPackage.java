package uk.org.taverna.scufl2.ucfpackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;

import uk.org.taverna.scufl2.ucfpackage.impl.odfdom.pkg.OdfPackage;
import uk.org.taverna.scufl2.ucfpackage.impl.odfdom.pkg.manifest.OdfFileEntry;

public class UCFPackage {

	private static final Charset UTF_8 = Charset.forName("utf-8");
	public static final String MIME_BINARY = "application/octet-stream";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String MIME_TEXT_XML = "text/xml";
	public static final String MIME_RDF = "application/rdf+xml";
	public static final String MIME_EPUB = "application/epub+zip";
	public static final String MIME_WORKFLOW_BUNDLE = "application/vnd.taverna.workflow-bundle";
	public static final String MIME_DATA_BUNDLE = "application/vnd.taverna.data-bundle";
	public static final String MIME_WORKFLOW_RUN_BUNDLE = "application/vnd.taverna.workflow-run-bundle";
	public static final String MIME_SERVICE_BUNDLE = "application/vnd.taverna.service-bundle";

	private static Charset ASCII = Charset.forName("ascii");
	private final OdfPackage odfPackage;

	public UCFPackage() throws Exception {
		odfPackage = OdfPackage.create();
		odfPackage.setMediaType(MIME_EPUB);
	}

	public UCFPackage(File containerFile) throws Exception {
		odfPackage = OdfPackage.loadPackage(containerFile);
	}

	public UCFPackage(InputStream inputStream) throws Exception {
		odfPackage = OdfPackage.loadPackage(inputStream);
	}

	public String getPackageMediaType() {
		return odfPackage.getMediaType();
	}

	public void setPackageMediaType(String mediaType) {
		if (mediaType == null || !mediaType.contains("/")) {
			throw new IllegalArgumentException("Invalid media type " + mediaType);
		}
		if (!ASCII.newEncoder().canEncode(mediaType)) {
			throw new IllegalArgumentException("Media type must be ASCII: "
					+ mediaType);
		}
		odfPackage.setMediaType(mediaType);
	}

	public void save(File packageFile) throws IOException {
		File tempFile = File.createTempFile(packageFile.getName(), ".tmp",
				packageFile.getParentFile());

		try {
			odfPackage.save(tempFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not save bundle to " + packageFile, e);
		}
		tempFile.renameTo(packageFile);
	}

	public void insert(String stringValue, String path, String mediaType)
			throws Exception {
		odfPackage.insert(stringValue.getBytes(UTF_8), path, mediaType);
	}

	public void insert(byte[] bytesValue, String path, String mediaType)
			throws Exception {
		odfPackage.insert(bytesValue, path, mediaType);
	}

	public void insert(Document document, String path, String mediaType)
			throws Exception {
		odfPackage.insert(document, path, mediaType);
	}

	public void insert(InputStream inputStream, String path, String mediaType)
			throws Exception {
		odfPackage.insert(inputStream, path, mediaType);
	}

	public void insert(URI uri, String path, String mediaType) throws Exception {
		odfPackage.insert(uri, path, mediaType);
	}

	public String getEntryAsString(String path) throws Exception {
		return new String(odfPackage.getBytes(path), UTF_8);
	}

	public byte[] getEntryAsBytes(String path) throws Exception {
		return odfPackage.getBytes(path);
	}

	public InputStream getEntryAsInputStream(String path) throws Exception {
		return odfPackage.getInputStream(path);
	}

	public Map<String, ResourceEntry> listContent() {
		return listContent("", false);
	}

	public Map<String, ResourceEntry> listContent(String folderPath) {
		return listContent(folderPath, false);
	}

	protected Map<String, ResourceEntry> listContent(String folderPath,
			boolean recursive) {
		if (!folderPath.isEmpty() && !folderPath.endsWith("/")) {
			folderPath = folderPath + "/";
		}
		HashMap<String, ResourceEntry> content = new HashMap<String, ResourceEntry>();

		for (Entry<String, OdfFileEntry> entry : odfPackage
				.getManifestEntries().entrySet()) {
			String entryPath = entry.getKey();
			if (!entryPath.startsWith(folderPath)) {
				continue;
			}
			String subPath = entryPath
					.substring(folderPath.length(), entryPath.length());
			if (subPath.isEmpty()) {
				// The folder itself
				continue;
			}
			int firstSlash = subPath.indexOf("/");
			if (!recursive && firstSlash > -1
					&& firstSlash < subPath.length() - 1) {
				// Children of a folder (note that we'll include the folder
				// itself which ends in /)
				continue;
			}
			content.put(subPath, new ResourceEntry(entry.getValue()));
		}
		return content;
	}

	public class ResourceEntry {

		private final String path;
		private final long size;
		private final String mediaType;

		protected ResourceEntry(OdfFileEntry odfEntry) {
			path = odfEntry.getPath();
			size = odfEntry.getSize();
			mediaType = odfEntry.getMediaType();
		}

		public String getPath() {
			return path;
		}

		public long getSize() {
			return size;
		}

		public String getMediaType() {
			return mediaType;
		}
	}

	public Map<String, ResourceEntry> listContentRecursive() {
		return listContent("", true);
	}

}