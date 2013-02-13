package powercrystals.core.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;

public class UpdateCheckThread extends Thread
{
	private static final String _releaseUrlBaseDefault = "https://raw.github.com/powercrystals/";
	private static final String _releaseUrlSuffixDefault = "/master/VERSION";
	
	private String _releaseUrlBase;
	private String _releaseUrlSuffix;
	private IUpdateableMod _mod;
	
	private boolean _checkComplete;
	private boolean _newVerAvailable;
	private ModVersion _newVer;
	
	public UpdateCheckThread(IUpdateableMod mod)
	{
		this(mod, _releaseUrlBaseDefault, _releaseUrlSuffixDefault);
	}
	
	public UpdateCheckThread(IUpdateableMod mod, String releaseUrlBase, String releaseUrlSuffix)
	{
		_mod = mod;
		_releaseUrlBase = releaseUrlBase;
		_releaseUrlSuffix = releaseUrlSuffix;
	}
	
	@Override
	public void run()
	{
		try
		{
			URL versionFile = new URL(_releaseUrlBase + _mod.getModFolder() + _releaseUrlSuffix);

			BufferedReader reader = new BufferedReader(new InputStreamReader(versionFile.openStream()));
			ModVersion newVer = ModVersion.parse(reader.readLine());
			ModVersion ourVer = ModVersion.parse(_mod.getModVersion());
			reader.close();

			if(ourVer.compareTo(newVer) < 0)
			{
				FMLLog.log(Level.INFO, "An updated version of " + _mod.getModName() + " is available: " + newVer.modVersion().toString() + ".");
				if(ourVer.minecraftVersion().compareTo(newVer.minecraftVersion()) < 0)
				{
					FMLLog.log(Level.INFO, "This update is for Minecraft " + newVer.minecraftVersion().toString() + ".");
				}
			}
			
			_newVer = newVer;
			_newVerAvailable = ourVer.compareTo(newVer) < 0;
			_checkComplete = true;

		}
		catch (Exception e)
		{
			FMLLog.log(Level.WARNING, "Update check for " + _mod.getModName() + " failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean checkComplete()
	{
		return _checkComplete;
	}
	
	public boolean newVersionAvailable()
	{
		return _newVerAvailable;
	}
	
	public ModVersion newVersion()
	{
		return _newVer;
	}
}