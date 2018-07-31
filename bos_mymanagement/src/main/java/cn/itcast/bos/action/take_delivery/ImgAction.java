package cn.itcast.bos.action.take_delivery;

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class ImgAction {

    private File imgFile;
    private String imgFileFileName;

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public void setImgFileFileName(String imgFileFileName) {
        this.imgFileFileName = imgFileFileName;
    }

    @Action(value = "imgUpload", results = {@Result(name = "success", type = "json")})
    public String upload() throws Exception {
        System.out.println("文件名" + imgFileFileName);
        //文件保存目录路径
        String savePath = ServletActionContext.getServletContext().getRealPath("/") + "upload/";
        //文件保存目录URL
        String saveUrl = ServletActionContext.getRequest().getContextPath() + "/upload/";
        //生成随机图片名字
        UUID randomUUID = UUID.randomUUID();
        String ext = imgFileFileName.substring(imgFileFileName.lastIndexOf("."));
        String randomFileName = randomUUID + ext;

        //保存图片（绝对路径）
        FileUtils.copyFile(imgFile, new File(savePath + randomFileName));

        Map<String, Object> result = new HashMap<>();
        result.put("error", 0);
        result.put("url", saveUrl + randomFileName);
        ActionContext.getContext().getValueStack().push(result);
        return "success";
    }

    @Action(value = "imgManage", results = {@Result(name = "success", type = "json")})
    public String manage() {

        String rootPath = ServletActionContext.getServletContext().getRealPath("/") + "upload/";

        String rootUrl = ServletActionContext.getRequest().getContextPath() + "/upload/";
        //图片扩展名
        String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};

        File currentPathFile = new File(rootPath);

//遍历目录取的文件信息
        List<Hashtable> fileList = new ArrayList<Hashtable>();
        if (currentPathFile.listFiles() != null) {
            for (File file : currentPathFile.listFiles()) {
                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                String fileName = file.getName();
                if (file.isDirectory()) {
                    hash.put("is_dir", true);
                    hash.put("has_file", (file.listFiles() != null));
                    hash.put("filesize", 0L);
                    hash.put("is_photo", false);
                    hash.put("filetype", "");
                } else if (file.isFile()) {
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    hash.put("is_dir", false);
                    hash.put("has_file", false);
                    hash.put("filesize", file.length());
                    hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
                    hash.put("filetype", fileExt);
                }
                hash.put("filename", fileName);
                hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
                fileList.add(hash);
            }
        }


        Map<String,Object> result = new HashMap<>();
        result.put("moveup_dir_path", "");
        result.put("current_dir_path", rootPath);
        result.put("current_url", rootUrl);
        result.put("total_count", fileList.size());
        result.put("file_list", fileList);
        ActionContext.getContext().getValueStack().push(result);
        return "success";
    }
}
