package com.schimpf.block.braj.pro;

import java.io.File;
import java.io.Serializable;

// This class is used to pass non-primitives in bundles.

public class SBundle implements Serializable
{
	private static final long serialVersionUID = 1L;
	File file;
	
	public SBundle(File file)
	{
		this.file = file;
	}
}
