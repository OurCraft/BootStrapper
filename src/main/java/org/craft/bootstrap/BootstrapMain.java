package org.craft.bootstrap;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import org.json.*;

public class BootstrapMain
{

    public static void main(String[] args)
    {
        BootstrapFrame frame = new BootstrapFrame();
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try
        {
            frame.log("Getting infos from remote...");
            JSONObject object = new JSONObject(new String(read("https://raw.githubusercontent.com/OurCraft/BootStrapper/master/bootstrap.json"), "UTF-8"));
            String link = "https://drone.io/github.com/OurCraft/OurCraftLauncher/files/build/libs/" + object.getString("launcher");
            File folder = null;
            String appdata = System.getenv("APPDATA");
            if(appdata != null)
                folder = new File(appdata, ".ourcraft");
            else
                folder = new File(System.getProperty("user.home"), ".ourcraft");
            if(!folder.exists())
                folder.mkdirs();
            frame.log("Downloading required libraries");
            JSONArray dependencies = object.getJSONArray("libraries");
            for(int i = 0; i < dependencies.length(); i++ )
            {
                String dependency = dependencies.getString(i);
                String split[] = dependency.split(":");
                String path = split[0].replace('.', '/') + "/" + split[1] + "/" + split[2] + "/" + split[1] + "-" + split[2];
                String name = split[1] + " " + split[2];
                path += ".jar";
                frame.log("Downloading library " + name + " from " + "http://repo1.maven.org/maven2/" + path);

                File file = new File(folder, "libraries/" + path);
                if(!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                if(!file.exists())
                {
                    file.createNewFile();
                    copy("http://repo1.maven.org/maven2/" + path, file);
                    frame.log("Library " + name + " done downloading");
                }
                else
                {
                    frame.log("Library " + name + " already exists, using local copy");
                }
                injectIntoClasspath(file);
            }

            frame.log("Verifying local copy of launcher... Requested file is " + object.getString("launcher"));
            File file = new File(new File(folder, "/versions/launcher/"), object.getString("launcher"));
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!file.exists())
            {
                frame.log("Downloading launcher jar file...");
                file.createNewFile();
                copy(link, file);
                frame.log("Done downloading launcher");
            }
            else
                frame.log("Launcher jar file already exists, using local copy.");

            injectIntoClasspath(file);
            frame.log("Initializing launcher");

            Class<?> clazz = Class.forName("org.craft.launch.OurCraftLauncher");
            Method mainMethod = clazz.getMethod("main", String[].class);
            mainMethod.setAccessible(true);
            frame.dispose();
            mainMethod.invoke(null, new Object[]
            {
                    new String[0]
            });
            System.exit(0);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void injectIntoClasspath(File file) throws Exception
    {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]
        {
                URL.class
        });
        addURL.setAccessible(true);
        addURL.invoke(classLoader, new Object[]
        {
                file.toURI().toURL()
        });
    }

    private static void copy(String link, File file) throws Exception
    {
        URL url = new URL(link);
        InputStream in = new BufferedInputStream(url.openStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[65565];
        int i;
        while((i = in.read(buffer)) != -1)
            out.write(buffer, 0, i);
        out.flush();
        out.close();
        in.close();
    }

    private static byte[] read(String path) throws Exception
    {
        URL url = new URL(path);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[65565];
        int i;
        while((i = in.read(buffer)) != -1)
            out.write(buffer, 0, i);
        out.flush();
        out.close();
        in.close();

        return out.toByteArray();
    }
}
