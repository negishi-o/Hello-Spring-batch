package com.udemy.HelloBatch.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class HelloJobParameterValidator implements JobParametersValidator{

	@Override
	public void validate(JobParameters parameters) 
			throws JobParametersInvalidException {
		String param1 = parameters.getString("param1");
		if(!param1.equals("DEV")&&
				!param1.equals("TEST")&&
				!param1.equals("PROD")) {
			throw new JobParametersInvalidException("param1:" + param1
					+ "DEV/TEST/PRODのみを指定して下さい！");
		}
		
		String param2 = parameters.getString("param2");
		
		try {
			Integer.parseInt(param2);
		} catch(Exception e) {
			throw new JobParametersInvalidException("param2:" + param2
					+ "数値のみ指定が可能です！");
		}
		
	}

}
