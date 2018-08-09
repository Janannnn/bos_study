package cn.itcast.service;

import cn.itcast.bos.domain.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImgServiceImpl implements ImgService {
    @Override
    public String imgUpload(File img) {
        try {
            //文件保存目录路径
            String savePath = ServletActionContext.getServletContext().getRealPath("/") + "upload/";
            //文件保存目录URL
            String saveUrl = ServletActionContext.getRequest().getContextPath() + "/upload/";
            //生成随机图片名字
            UUID randomUUID = UUID.randomUUID();
            String randomFileName = randomUUID+".jpg";

            //保存图片（绝对路径）
            FileUtils.copyFile(img, new File(savePath + randomFileName));
            String url = Constants.BOS_IMG_URL+randomFileName;
            System.out.println(url);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
