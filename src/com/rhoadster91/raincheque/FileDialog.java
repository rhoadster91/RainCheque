package com.rhoadster91.raincheque;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.rhoadster91.raincheque.ListenerList.FireHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.preference.PreferenceManager;

public class FileDialog
{
    private static final String PARENT_DIR = "..";
    private List<String> fileList;
    private File currentPath;
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final Activity activity;
    protected boolean selectDirectoryOption;
    protected String fileEndsWith;    
    private int directoryCount = 0;

    public interface FileSelectedListener
    {
        void fileSelected(File file);
    }
    
    public interface DirectorySelectedListener 
    {
        void directorySelected(File directory);
    }
    
    public FileDialog(Activity activity, File path) 
    {
        this.activity = activity;
        if (!path.exists()) 
        	path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public Dialog createFileDialog() 
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        List<Integer> icons = new ArrayList<Integer>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
		int fileIcon, folderIcon;
        if(sharedPref.getBoolean("black theme", false))
		{
			fileIcon = R.drawable.file_dark;
			folderIcon = R.drawable.folder_dark;
		}
		else
		{
			fileIcon = R.drawable.file_light;
			folderIcon = R.drawable.folder_light;
		}
        for(int i=0;i<fileList.size();i++)
        {
        	
        	
        	if(i<directoryCount)
        		icons.add(folderIcon);
        	else
        		icons.add(fileIcon);
        }
        FileListAdapter adapter = new FileListAdapter(activity, fileList, icons);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
                String fileChosen = fileList.get(which);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) 
                {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                }
                else
                	fireFileSelectedEvent(chosenFile);
            }
        });
        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) 
        {
            builder.setPositiveButton("Select directory", new OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int which) 
                {
                   fireDirectorySelectedEvent(currentPath);
                }
            });
        }
        dialog = builder.show();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) 
    {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) 
    {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) 
    {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) 
    {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) 
    {
        dirListenerList.remove(listener);
    }

    public void showDialog() 
    {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) 
    {
        fileListenerList.fireEvent(new FireHandler<FileDialog.FileSelectedListener>() 
        {
            public void fireEvent(FileSelectedListener listener) 
            {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) 
    {
        dirListenerList.fireEvent(new FireHandler<FileDialog.DirectorySelectedListener>() 
        {
            public void fireEvent(DirectorySelectedListener listener) 
            {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) 
    {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) 
        {
            if (path.getParentFile() != null) 
            	r.add(PARENT_DIR);
            FilenameFilter directoryFilter = new FilenameFilter() 
            {
                public boolean accept(File dir, String filename) 
                {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) 
                    	return false;
                    return sel.isDirectory();                    
                }
            };
            FilenameFilter filter = new FilenameFilter() 
            {
                public boolean accept(File dir, String filename) 
                {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) 
                    	return false;
                    boolean endsWith = fileEndsWith != null ? filename.toLowerCase(Locale.getDefault()).endsWith(fileEndsWith) : false;
                    return endsWith;                    
                }
            };
            String[] directoryList = path.list(directoryFilter);            
            Arrays.sort(directoryList);
            String[] restFileList = path.list(filter);
            Arrays.sort(restFileList);
            for(String file : directoryList)
            	r.add(file);
            directoryCount = r.size();
            if(!selectDirectoryOption)
            {
            	for (String file : restFileList)             
            		r.add(file);
            }
            
        }
        
        fileList = r;
    }

    private File getChosenFile(String fileChosen) 
    {
        if (fileChosen.equals(PARENT_DIR)) 
        	return currentPath.getParentFile();
        else
        	return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) 
    {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase(Locale.getDefault()) : fileEndsWith;
    }
 }

class ListenerList<L> 
{
	private List<L> listenerList = new ArrayList<L>();

	public interface FireHandler<L> 
	{
		void fireEvent(L listener);
	}

	public void add(L listener) 
	{
		listenerList.add(listener);
	}

	public void fireEvent(FireHandler<L> fireHandler) 
	{
		List<L> copy = new ArrayList<L>(listenerList);
		for (L l : copy) 
		{
			fireHandler.fireEvent(l);
		}
	}

	public void remove(L listener) 
	{
		listenerList.remove(listener);
	}

	public List<L> getListenerList() 
	{
		return listenerList;
	}
}