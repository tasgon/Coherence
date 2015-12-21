package org.tasgo.coherence.coremod;

import java.io.File;
import java.util.Map;

import org.tasgo.coherence.Coherence;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("CoherenceCore")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "org.tasgo.coherence.coremod." })
@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE - 80)
public class CoherenceLoadPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		// TODO Auto-generated method stub
		return new String[] { CoherenceTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		File coremodLocation = (File) data.get("coremodLocation");
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
