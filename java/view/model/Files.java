package view.model;

import java.util.List;

/** A model describing how the list of files looks like. */
public class Files {
	private int numberOfFiles;
	private List<File> files;
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
}
