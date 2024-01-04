package com.github.xuchen93.generate.service;

import com.github.xuchen93.generate.model.GenerateInfo;
import com.github.xuchen93.generate.util.GenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;

@Service
public class GenerateService {

	@Autowired
	DataSourceProperties dataSourceProperties;

	public void generate(GenerateInfo info) {
		info.setDBProperties(dataSourceProperties);
		GenerateUtil.generate(info);
	}
}
