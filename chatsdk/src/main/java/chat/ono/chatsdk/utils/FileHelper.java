package chat.ono.chatsdk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class FileHelper {
	private static final int FILE_BUFFER_SIZE = 51200;
	
	
	public static boolean fileIsExist(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			Log.e("IM", "param invalid, filePath: " + filePath);
			return false;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			return false;
		}
		return true;
	}
	
	public static InputStream readFile(String filePath) {
		if (null == filePath) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return null;
		}

		InputStream is = null;

		try {
			if (fileIsExist(filePath)) {
				File f = new File(filePath);
				is = new FileInputStream(f);
			} else {
				return null;
			}
		} catch (Exception ex) {
			Log.e("IM", "Exception, ex: " + ex.toString());
			return null;
		}
		return is;
	}
	
	public static boolean createDirectory(String filePath){
		if (null == filePath) {
			return false;
		}

		File file = new File(filePath);

		if (file.exists()){
			return true;
		}
		
		return file.mkdirs();

	}
	
	public static boolean deleteDirectory(String filePath) {
		if (null == filePath) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return false;
		}

		File file = new File(filePath);

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			File[] list = file.listFiles();

			for (int i = 0; i < list.length; i++) {
				Log.d("IM", "delete filePath: " + list[i].getAbsolutePath());
				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}

		Log.d("IM", "delete filePath: " + file.getAbsolutePath());
		file.delete();
		return true;
	}
	
	
	public static void aa(Context context){
		context.getFilesDir();
	}
	public static boolean writeFile(String filePath, InputStream inputStream) {

		if (null == filePath || filePath.length() < 1) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return false;
		}		
		
		try {
			  File file = new File(filePath);
			  if (file.exists()) {
		    	   deleteDirectory(filePath);		
		       }	 
		       
		       String pth = filePath.substring(0, filePath.lastIndexOf("/"));
		       boolean ret = createDirectory(pth);
		       if (!ret){
		    	   Log.e("IM", "createDirectory fail path = " + pth);
		    	   return false;
		       }
		       
		       boolean ret1 = file.createNewFile();
		       if (!ret){
		    	   Log.e("IM", "createNewFile fail filePath = " + filePath);
		    	   return false;
		       } 

		       
		       FileOutputStream fileOutputStream = new FileOutputStream(file);
			    byte[] buf = new byte[1024];
				int c = inputStream.read(buf);
				while(-1 != c) {
					fileOutputStream.write(buf, 0, c);
					c = inputStream.read(buf);
				}
				
			       
				fileOutputStream.flush();
				fileOutputStream.close();
				
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	
	}
	
	public static boolean writeFile(String filePath, String fileContent) {
		return writeFile(filePath, fileContent, false);
	}
	
	public static boolean writeFile(String filePath, String fileContent, boolean append) {
		if (null == filePath || fileContent == null || filePath.length() < 1 || fileContent.length() < 1) {
			Log.e("IM", "Invalid param. filePath: " + filePath + ", fileContent: " + fileContent);
			return false;
		}
		
		try {
	       File file = new File(filePath);
	       if (!file.exists()) {
	    	   if (!file.createNewFile()) {
	    		   return false;
	    	   }
	       }
	       
	       BufferedWriter output = new BufferedWriter(new FileWriter(file, append));
	       output.write(fileContent);
	       output.flush();
	       output.close();
		} catch (IOException ioe) {
			Log.e("IM", "writeFile ioe: " + ioe.toString());
			return false;
		}
		
		return true;
	}
	
	public static long getFileSize(String filePath) {
		if (null == filePath) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return 0;
		}
		
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return 0;
		}

		return file.length();
	}
	
	public static long getFileModifyTime(String filePath) {
		if (null == filePath) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return 0;
		}
		
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return 0;
		}

		return file.lastModified();
	}
	
	public static boolean setFileModifyTime(String filePath, long modifyTime) {
		if (null == filePath) {
			Log.e("IM", "Invalid param. filePath: " + filePath);
			return false;
		}
		
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return false;
		}

		return file.setLastModified(modifyTime);
	}
	
	public static boolean copyFile(ContentResolver cr, String fromPath, String destPath) {
		if (null == cr || null == fromPath || fromPath.length() < 1 || null == destPath || destPath.length() < 1) {
			Log.e("IM", "copyFile Invalid param. cr="+cr+", fromPath="+fromPath+", destPath="+destPath);
			return false;
		}
		
		InputStream is = null;
		OutputStream os = null;
		try {
			
			
			// check output uri
			Uri formUri = null;
			if (fromPath.toLowerCase().startsWith("content://")) {
				formUri = Uri.parse(fromPath);
			} else if (fromPath.toLowerCase().startsWith("file://")) {
				fromPath = Uri.parse(fromPath).getPath();
			}
			
			Uri toUri = null;
			if (destPath.toLowerCase().startsWith("content://")) {
				toUri = Uri.parse(destPath);
			} else if (destPath.toLowerCase().startsWith("file://")) {
				destPath =  Uri.parse(destPath).getPath();
			}
			
			// open input 
			if (formUri == null) {
				is = new FileInputStream(fromPath);
			} else {
				is = new ParcelFileDescriptor.AutoCloseInputStream(cr.openFileDescriptor(formUri, "r"));
			}
			
			// open output 
			if (toUri == null) {
				File fl = new File(destPath);
	            String pth = destPath.substring(0, destPath.lastIndexOf("/"));
	            File pf = new File(pth);
	            
	            if (pf.exists() && !pf.isDirectory()) {
	            	pf.delete();
	            }
	            
	            pf = new File(pth+File.separator);
	            
	            if (!pf.exists()) {
	                if (!pf.mkdirs()) {
	                    Log.e("IM", "Can't make dirs, path=" + pth);
	                }
	            }
	            
	            pf = new File(destPath);
	            if (pf.exists()) {
	            	if (pf.isDirectory()) deleteDirectory(destPath);
	            	else pf.delete();
	            }
	            
				os = new FileOutputStream(destPath);
				fl.setLastModified(System.currentTimeMillis());
			} else {
				os = new ParcelFileDescriptor.AutoCloseOutputStream(cr.openFileDescriptor(toUri, "w"));
			}
			
			// copy file
			byte[] dat = new byte[1024];
			int i = is.read(dat);
			while(-1 != i) {
				os.write(dat, 0, i);
				i = is.read(dat);
			}
			
			is.close();
			is = null;
			
			os.flush();
			os.close();
			os = null;
			
			return true;
			
		} catch(Exception ex) {
			Log.e("IM", "Exception, ex: " + ex.toString());
		} finally {
			if(null != is) {
				try{is.close();} catch(Exception ex) {};
			}
			if(null != os) {
				try{os.close();} catch(Exception ex) {};
			}
		}
		return false;
	}
	
	public static byte[] readAll(InputStream is) throws Exception {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		byte[] buf = new byte[1024];
		int c = is.read(buf);
		while(-1 != c) {
			baos.write(buf, 0, c);
			c = is.read(buf);
		}
		baos.flush();
		baos.close();
		return baos.toByteArray();
    }
	
	public static byte[] readFile(Context ctx, Uri uri) {
		if(null == ctx || null == uri) {
			Log.e("IM", "Invalid param. ctx: "+ctx+", uri: "+uri);
			return null;
		}
		
		InputStream is = null;
		String scheme = uri.getScheme().toLowerCase();
		if (scheme.equals("file")) {
			is = readFile(uri.getPath());
		}

		try {
			is = ctx.getContentResolver().openInputStream(uri);
			if(null == is) {
				return null;
			}
			
			byte[] bret = readAll(is);
			is.close(); is = null;
			
			return bret;
		} catch(FileNotFoundException fne) {
			Log.e("IM", "FilNotFoundException, ex: " + fne.toString());
		} catch(Exception ex) {
			Log.e("IM", "Exception, ex: " + ex.toString());
		} finally {
			if (null != is) {
				try {is.close();} catch(Exception ex) {};
			}
		}
		return null;
	}
	
	public static String readAssetsFile(Context ctx, String filename) {
		StringBuilder result = new StringBuilder();
		try {
			InputStreamReader inputReader = new InputStreamReader(ctx.getResources().getAssets().open(filename));
			BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
            	result.append(line);
            	result.append("\n");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public static boolean writeFile(String filePath, byte[] content) {
		if(null == filePath || null == content) {
			Log.e("IM", "Invalid param. filePath: "+filePath+", content: "+content);
			return false;
		}
		
		FileOutputStream fos = null;
		try{
			String pth = filePath.substring(0, filePath.lastIndexOf("/"));
            File pf = null;
            pf = new File(pth);
            if(pf.exists() && !pf.isDirectory()) {
            	pf.delete();
            }
            pf = new File(filePath);
            if(pf.exists()) {
            	if(pf.isDirectory()) FileHelper.deleteDirectory(filePath);
            	else pf.delete();
            }

            pf = new File(pth+File.separator);
            if (!pf.exists()) {
                if (!pf.mkdirs()) {
                    Log.e("IM", "Can't make dirs, path=" + pth);
                }
            }
            
			fos = new FileOutputStream(filePath);
			fos.write(content);
			fos.flush();
			fos.close(); fos = null;
			pf.setLastModified(System.currentTimeMillis());
			
			return true;
			
		} catch(Exception ex) {
			Log.e("IM", "Exception, ex: " + ex.toString());
		} finally {
			if(null != fos) {
				try { fos.close(); } catch(Exception ex) {};
			}
		}
		return false;
	}
	
	/*************ZIP file operation***************/
	public static boolean readZipFile(String zipFileName, StringBuffer crc) {
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				long size = entry.getSize();
				crc.append(entry.getCrc() + ", size: " + size);
	        }
	        zis.close();
		} catch (Exception ex) {
			Log.e("IM", "Exception: " + ex.toString());
			return false;
		}
		return true;
	}
	
	public static byte[] readGZipFile (String zipFileName) {
		if (fileIsExist(zipFileName)) {
			Log.i("IM", "zipFileName: " + zipFileName);
			try {
				FileInputStream fin = new FileInputStream(zipFileName);
				int size;
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((size = fin.read(buffer, 0, buffer.length)) != -1) {
					baos.write(buffer, 0, size);
				}
				return baos.toByteArray();
			} catch (Exception ex) {
				Log.i("IM", "read zipRecorder file error");
			}
		}
		return null;
	}
	
	public static boolean zipFile(String baseDirName, String fileName, String targerFileName) throws IOException {
       if (baseDirName == null || "".equals(baseDirName)) {
           return false;
       }
       File baseDir = new File(baseDirName);
       if (!baseDir.exists() || !baseDir.isDirectory()) {
           return false;
       }

       String baseDirPath = baseDir.getAbsolutePath();
       File targerFile = new File(targerFileName);
       ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targerFile));
       File file = new File(baseDir, fileName);

       boolean zipResult = false;
       if (file.isFile()) {
    	   zipResult = fileToZip(baseDirPath, file, out);
       } else {
    	   zipResult = dirToZip(baseDirPath, file, out);
       }
       out.close();
       return zipResult;
   }
	
	public static boolean unZipFile(String fileName, String unZipDir) throws Exception {
		File f = new File(unZipDir);

		if (!f.exists()) {
			f.mkdirs();
		}

		BufferedInputStream is = null;
		ZipEntry entry;
		ZipFile zipfile = new ZipFile(fileName);
		Enumeration<?> enumeration = zipfile.entries();
		byte data[] = new byte[FILE_BUFFER_SIZE];
		Log.i("IM", "unZipDir: " + unZipDir);

		while (enumeration.hasMoreElements()) {
			entry = (ZipEntry)enumeration.nextElement();

			if (entry.isDirectory()) {
				File f1 = new File(unZipDir + "/" + entry.getName());
				Log.i("IM", "entry.isDirectory XXX " + f1.getPath());
				if (!f1.exists()) {
					f1.mkdirs();
				}
			} else {
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				String name = unZipDir + "/" + entry.getName();
				RandomAccessFile m_randFile = null;
				File file = new File(name);
				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();
				m_randFile = new RandomAccessFile(file, "rw");
				int begin = 0;

				while ((count = is.read(data, 0, FILE_BUFFER_SIZE)) != -1) {
					try {
						m_randFile.seek(begin);
					} catch (Exception ex) {
						Log.e("IM", "exception, ex: " + ex.toString());
					}

					m_randFile.write(data, 0, count);
					begin = begin + count;
				}
				
				file.delete();
				m_randFile.close();
				is.close();
			}
		}

		return true;
	}
	
	private static boolean fileToZip(String baseDirPath, File file, ZipOutputStream out) throws IOException {
       FileInputStream in = null;
       ZipEntry entry = null;

       byte[] buffer = new byte[FILE_BUFFER_SIZE];
       int bytes_read;
       try {
           in = new FileInputStream(file);
           entry = new ZipEntry(getEntryName(baseDirPath, file));
           out.putNextEntry(entry);
           
           while ((bytes_read = in.read(buffer)) != -1) {
               out.write(buffer, 0, bytes_read);
           }
           out.closeEntry();
           in.close();
       } catch (IOException e) {
    	   Log.e("IM", "Exception, ex: " + e.toString());
    	   return false;
       } finally {
           if (out != null) {
               out.closeEntry();
           }

           if (in != null) {
               in.close();
           }
       }
       return true;
   }
	
	private static boolean dirToZip(String baseDirPath, File dir, ZipOutputStream out) throws IOException {
       if (!dir.isDirectory()) {
           return false;
       }

       File[] files = dir.listFiles();
       if (files.length == 0) {
           ZipEntry entry = new ZipEntry(getEntryName(baseDirPath, dir));

           try {
               out.putNextEntry(entry);
               out.closeEntry();
           } catch (IOException e) {
        	   Log.e("IM", "Exception, ex: " + e.toString());
           }
       }
       
       for (int i = 0; i < files.length; i ++) {
           if (files[i].isFile()) {
               fileToZip(baseDirPath, files[i], out);
           } else {
               dirToZip(baseDirPath, files[i], out);
           }
       }
       return true;
   }
	
	private static String getEntryName(String baseDirPath, File file) {
       if (!baseDirPath.endsWith(File.separator)) {
           baseDirPath = baseDirPath + File.separator;
       }

       String filePath = file.getAbsolutePath();
       if (file.isDirectory()) {
           filePath = filePath + "/";
       }

       int index = filePath.indexOf(baseDirPath);
       return filePath.substring(index + baseDirPath.length());
   }

	public static void moveFile(String fromPath, String destPath) {
		
		InputStream in = null;
	    OutputStream out = null;
	    try {

	        //create output directory if it doesn't exist
	    	String destDir = destPath.substring(0, destPath.lastIndexOf("/") + 1);
	        File dir = new File (destDir); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }


	        in = new FileInputStream(destPath);        
	        out = new FileOutputStream(destPath);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	            // write the output file
	            out.flush();
	        out.close();
	        out = null;

	        // delete the original file
	        new File(fromPath).delete();  


	    }   catch (FileNotFoundException fnfe1) {
//	        Log.e("IM", "tag", fnfe1.getMessage());
	    } catch (Exception e) {
//	        Log.e("IM", "tag", e.getMessage());
	    }

	}

	
}