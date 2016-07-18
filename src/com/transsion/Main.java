package com.transsion;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main {

	// private static LinkedList<File> mFilesToGrep;
	// private static LinkedList<File> mPngFiles;
	//
	// private static LinkedList<File> mRedundentFiles;
	// private static HashSet<String> mPngNames;
	//
	// private static HashSet<String> mRedundentPngNames;
	// private static HashMap<String, LinkedList<File>> mNameFiles;
	// private static HashMap<String, LinkedList<File>> mRedundentNameFiles;

	private static boolean mIsToRemove;
	private static LinkedList<File> mAppPaths;

	private static File mDir = new File(".");
	private static final String[] SRC_SUFIX = new String[] { ".java", ".xml" };
	private static final String[] RES_SUFIX = new String[] { ".png" };
	private static final String[] ANDROID_MANIFEST_SUFIX = new String[] { "AndroidManifest.xml" };

	// private static LinkedList<String> redundency

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		for (String arg : args) {
			if (arg.equals("--remove")) {
				mIsToRemove = true;
				System.out.println("Will remove redundent png files!");
			}
			if (arg.startsWith("--path")) {
				String path = arg.substring(6, arg.length());
				mDir = new File(path);
			}
		}
		
		System.out.println("Searching from " + mDir.getAbsolutePath());
		/*
		 * System.out.println("current path:" + getCurrentPath());
		 * 
		 * mFilesToGrep = getTypedFiles(mDir, SRC_SUFIX); //
		 * System.out.println("java file count:" // + (null == mJavaFiles ? 0 :
		 * mJavaFiles.size()));
		 * 
		 * mPngFiles = getTypedFiles(mDir, RES_SUFIX);
		 * 
		 * mPngNames = getNamesNoRepeated(mPngFiles); //
		 * System.out.println("png names count:" // + (null == mPngNames ? 0 :
		 * mPngNames.size()));
		 * 
		 * //mRedundentPngNames = getRedundentNames(mPngNames, mFilesToGrep); //
		 * System.out.println("Redundent name Count:" // + (null ==
		 * mRedundentPngNames ? 0 : mRedundentPngNames.size()));
		 * 
		 * mNameFiles = getNameFilesMap(mPngFiles); //
		 * System.out.println("name-files map size:" // + (null == mNameFiles ?
		 * 0 : mNameFiles.size()));
		 * 
		 * mRedundentNameFiles = getRedundentNameFilesMap(mNameFiles,
		 * mFilesToGrep); // System.out //
		 * .println("redundent name-files map size:" // + (null ==
		 * mRedundentNameFiles ? 0 // : mRedundentNameFiles.size()));
		 * 
		 * mRedundentFiles = getRedundentFiles(mRedundentNameFiles);
		 * 
		 * System.out.println("png file count:" + (null == mPngFiles ? 0 :
		 * mPngFiles.size())); System.out.println("redundent png file count:" +
		 * (null == mRedundentFiles ? 0 : mRedundentFiles.size()));
		 * 
		 * System.out.println("redundent png files list:"); if (mRedundentFiles
		 * != null) { for (File file : mRedundentFiles) {
		 * System.out.println(file.getAbsolutePath()); if (mIsToRemove) { if
		 * (file.delete()) { System.out .println(file.getAbsolutePath() +
		 * " deleted!"); } } } }
		 */
		mAppPaths = getAllAppProjects(mDir);
		if (null == mAppPaths || mAppPaths.size() == 0) {
			System.out.println("Not found any app project!");
			return;
		}
		System.out.println("Found " + mAppPaths.size() + " app project(s).");
		int number = 0;
		for (File project : mAppPaths) {
			System.out.println("\nNo." +  (++number) + " " + project.getAbsolutePath());
			handleOneProject(project, mIsToRemove);
		}
		System.out.println("\nAll is Done.");
	}

	private static void handleOneProject(File projectDir, boolean delete) {
		if (null == projectDir || !projectDir.exists()
				|| !projectDir.isDirectory()) {
			System.out.println("Invalid path!");
			return;
		}

		LinkedList<File> filesToGrep;
		LinkedList<File> pngFiles;
		LinkedList<File> redundentFiles;
		HashMap<String, LinkedList<File>> nameFiles;
		HashMap<String, LinkedList<File>> redundentNameFiles;

		filesToGrep = getTypedFiles(projectDir, SRC_SUFIX);

		pngFiles = getTypedFiles(projectDir, RES_SUFIX);

		nameFiles = getNameFilesMap(pngFiles);

		redundentNameFiles = getRedundentNameFilesMap(nameFiles, filesToGrep);

		redundentFiles = getRedundentFiles(redundentNameFiles);

		System.out.println("png file count:"
				+ (null == pngFiles ? 0 : pngFiles.size()));
		System.out.println("redundent png file count:"
				+ (null == redundentFiles ? 0 : redundentFiles.size()));
		System.out.println("redundent png files list:");

		if (redundentFiles != null) {
			for (File file : redundentFiles) {
				System.out.println(file.getAbsolutePath());
				if (delete) {
					if (file.delete()) {
						System.out
								.print(file.getAbsolutePath() + " Deleted!\n");
					}
				}
			}
		}
	}

	private static LinkedList<File> getRedundentFiles(
			HashMap<String, LinkedList<File>> redundentNameFiles) {
		if (null == redundentNameFiles) {
			return null;
		}
		LinkedList<File> redundentFiles = new LinkedList<File>();
		if (redundentNameFiles.keySet() != null) {
			for (String name : redundentNameFiles.keySet()) {
				redundentFiles.addAll(redundentNameFiles.get(name));
			}
		}
		return redundentFiles;
	}

	/*
	 * private static HashSet<String> getRedundentNames(HashSet<String>
	 * toPickUp, LinkedList<File> files) { if (null == toPickUp || null ==
	 * files) { return null; } HashSet<String> redundency = new
	 * HashSet<String>(); HashSet<String> refferred = new HashSet<String>();
	 * redundency.addAll(toPickUp); for (String name : toPickUp) { if
	 * (referredInFileList(name, files)) { refferred.add(name); } }
	 * redundency.removeAll(refferred); return redundency; }
	 */
	private static HashMap<String, LinkedList<File>> getRedundentNameFilesMap(
			HashMap<String, LinkedList<File>> toPickUp, LinkedList<File> files) {
		if (null == files || files.size() == 0 || null == toPickUp
				|| toPickUp.size() == 0) {
			return null;
		}
		HashMap<String, LinkedList<File>> redundentNameFiles = new HashMap<String, LinkedList<File>>();
		// HashMap<String, LinkedList<File>> refferredNameFiles = new
		// HashMap<String, LinkedList<File>>();

		for (String name : toPickUp.keySet()) {
			if (!referredInFileList(name, files)) {
				redundentNameFiles.put(name, toPickUp.get(name));
			}
		}
		return redundentNameFiles;
	}

	/*
	 * private static String getCurrentPath() { File directory = new File("");
	 * return directory.getAbsolutePath(); }
	 */

	private static LinkedList<File> getTypedFiles(File dir, String[] types) {
		if (null == dir || !dir.exists() || !dir.isDirectory() || null == types
				|| 0 == types.length) {
			return null;
		}
		LinkedList<File> result = new LinkedList<File>();

		LinkedList<File> folderList = new LinkedList<File>();
		folderList.add(dir);
		while (!folderList.isEmpty()) {
			File folder = folderList.removeFirst();
			File[] files = folder.listFiles();
			// System.out.println(folder.getAbsolutePath());
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						folderList.add(file);
					} else {
						String filePath = file.getAbsolutePath();
						for (String type : types) {
							if (filePath.endsWith(type)) {
								// System.out.println(filePath);
								result.add(file);
								break;
							}
						}
					}
				}
			}
		}

		return result;
	}

	private static LinkedList<File> getAllAppProjects(File rootDir) {
		if (null == rootDir || !rootDir.exists() || !rootDir.isDirectory()) {
			return null;
		}
		LinkedList<File> manifests = getTypedFiles(rootDir,
				ANDROID_MANIFEST_SUFIX);
		LinkedList<File> appPaths = new LinkedList<File>();
		for (File file : manifests) {
			//System.out.println(file.getAbsolutePath());
			//System.out.println("@" + file.getParentFile().getAbsolutePath());
			File parentFile = file.getParentFile();
			boolean isIndependent = true;
			for (File f : appPaths) {
				if( parentFile.getAbsolutePath().startsWith(f.getAbsolutePath())
						&& !parentFile.getParent().equals(f.getParent())) {
					isIndependent = false;
					break;
				}
			}
			if (isIndependent) {
				appPaths.add(parentFile);
			}
		}
		return appPaths;
	}

	/*
	 * private static HashSet<String> getNamesNoRepeated(List<File> files) { if
	 * (null == files) { return null; } HashSet<String> names = new
	 * HashSet<String>(); for (File file : files) {
	 * names.add(getFileNameNoEx(file.getName())); } return names; }
	 */

	private static HashMap<String, LinkedList<File>> getNameFilesMap(
			List<File> files) {
		if (null == files) {
			return null;
		}
		HashMap<String, LinkedList<File>> nameFiles = new HashMap<String, LinkedList<File>>();
		for (File file : files) {
			String name = getFileNameNoEx(file.getName());
			LinkedList<File> filesWithSameName = nameFiles.get(name);
			if (filesWithSameName == null) {
				filesWithSameName = new LinkedList<File>();
			}
			filesWithSameName.add(file);
			nameFiles.remove(name);
			nameFiles.put(name, filesWithSameName);
		}
		return nameFiles;
	}

	private static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.indexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	private static boolean referredInFileList(String key, List<File> files) {
		if (null == key || key.length() == 0 || null == files
				|| files.size() == 0) {
			System.out.println("Parametres error in referredInFiles()");
			return false;
		}
		for (File file : files) {
			if (referredInFile(file, key)) {
				return true;
			}
		}
		// System.out.println("Not found " + "\'" + key + "\'" +
		// " in java files of this project");
		return false;
	}

	private static boolean referredInFile(File file, String keyword) {
		verifyParam(file, keyword);
		LineNumberReader lineReader = null;
		try {
			lineReader = new LineNumberReader(new FileReader(file));
			String readLine = null;
			while ((readLine = lineReader.readLine()) != null) {
				if (readLine.indexOf(keyword) > -1) {
					// System.out.println(keyword + "|" + file.getName() + "|"
					// + readLine);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(lineReader);
		}
		// System.out.println("Not found " + "\'" + keyword + "\'" + " in " +
		// file.getName());
		return false;
	}

	private static void verifyParam(File file, String keyword) {

		if (file == null) {
			throw new NullPointerException("the file is null");
		}
		if (keyword == null || keyword.trim().equals("")) {
			throw new NullPointerException("the keyword is null or \"\" ");
		}

		if (!file.exists()) {
			throw new RuntimeException("the file is not exists");
		}

		if (file.isDirectory()) {
			throw new RuntimeException("the file is a directory,not a file");
		}

		if (!file.canRead()) {
			throw new RuntimeException("the file can't read");
		}
	}

	private static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
				c = null;
			}
		}
	}
}
