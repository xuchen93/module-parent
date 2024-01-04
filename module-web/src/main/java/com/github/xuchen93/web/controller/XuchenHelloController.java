package com.github.xuchen93.web.controller;

import com.github.xuchen93.model.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("xuchen")
public class XuchenHelloController extends BaseController {

	@GetMapping("hello")
	public R hello() {
		return R.success("hello xuchen");
	}
}
