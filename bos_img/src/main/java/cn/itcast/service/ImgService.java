package cn.itcast.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.File;

public interface ImgService {

    @Path("/imgUpload")
    @POST
    public String imgUpload(File img);
}
