package com.xxx.common.framework.base;

import com.xxx.common.bean.ResultData;
import com.xxx.common.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public class BaseController extends ResultUtil{
	public final static Logger log = LoggerFactory.getLogger(BaseController.class);

	//抓取错误处理
	public final ResultData<Object> paramsError(BindingResult result){
		return ResultUtil.paramsError(result.getFieldError().getDefaultMessage());
	}
}
