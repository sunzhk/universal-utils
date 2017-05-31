package com.sunzhk.tools.file;

import java.io.File;
import java.util.ArrayList;

public interface FileFilter {

		boolean filter(File file);

		void onAddMore(int currentNum);

		void filterResult(ArrayList<String> files);

	}