package com.oozie;

import org.apache.log4j.Logger;
import com.oozie.wfmanger.WfManager;
import com.oozie.wfmanger.WfManagerImpl;

public class DynWfGenerator {
	private static final Logger LOGGER = Logger.getLogger(DynWfGenerator.class);
	private static final String CONTEXT = "context";

	public static void main(String[] args) {

		WfManager wfManager = new WfManagerImpl();

		try {
			wfManager.generateWorkFlow();
		} catch (Exception e) {
			LOGGER.info(CONTEXT, e);
		}

	}

}
