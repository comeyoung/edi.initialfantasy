package org.edi.initialfantasy.service;

import org.edi.freamwork.cryptogram.MD5Util;
import org.edi.initialfantasy.bo.company.Company;
import org.edi.initialfantasy.bo.user.User;
import org.edi.initialfantasy.bo.userauthrization.UserAuth;
import org.edi.initialfantasy.data.DataConvert;
import org.edi.initialfantasy.data.ServicePath;
import org.edi.initialfantasy.dto.*;
import org.edi.initialfantasy.filter.UserRequest;
import org.edi.initialfantasy.repository.IBORepositoryCompany;
import org.edi.initialfantasy.repository.IBORepositoryUser;
import org.edi.initialfantasy.repository.IBORepositoryUserAuth;
import org.edi.initialfantasy.util.UUIDUtil;
import org.glassfish.jersey.server.JSONP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fancy
 * @date 2018/5/25
 */
@Path("/v1")
@Transactional
public class UserService implements IUserService{

    @Autowired
    private IBORepositoryUser boRepositoryUser;
    @Autowired
    private IBORepositoryUserAuth boRepositoryUserAuth;
    @Autowired
    private IBORepositoryCompany boRepositoryCompany;


    @POST
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/userauthrization")
    //用户登录
    public IResult<IUserAuthrizationResult> Login(Userauthrization userauthrization) {
        System.out.println(userauthrization);
        Result rs = new Result();
        UserAuthrizationResult uaResult = new UserAuthrizationResult();
        List<UserAuthrizationResult> listResult = new ArrayList<UserAuthrizationResult>();
        try {
            Company company = boRepositoryCompany.serchCompanyId(userauthrization.getCompanyName());
            User loginUser =  boRepositoryUser.getUserByCompanyId(userauthrization.getUserName(),company.getCompanyId());
            String hmacPassword = MD5Util.byteArrayToHexString(MD5Util.encryptHMAC(loginUser.getMobilePassword().getBytes(),"avatech"));
            if (hmacPassword.equals(userauthrization.getPassword())) {
                long NextDayTimeMillis = Long.parseLong(DataConvert.dateToStamp());
                UserAuth userRecord = boRepositoryUserAuth.serchLoginRecord(loginUser.getUserName());
                if(userRecord==null) {
                    String authToken = UUIDUtil.randomUUID32();
                    userRecord = new UserAuth(loginUser.getUserName(), loginUser.getIsMobileUser(), "客户", authToken, NextDayTimeMillis, "Y");
                    boRepositoryUserAuth.saveLoginRecord(userRecord);
                    uaResult = new UserAuthrizationResult(authToken,NextDayTimeMillis);
                }else{
                    Long currentTimeMillis = System.currentTimeMillis();
                    if(currentTimeMillis<userRecord.getAuthExpires()){
                        uaResult = new UserAuthrizationResult(userRecord.getAuthToken(),userRecord.getAuthExpires());
                    }else{
                        UserAuth userAuth = new UserAuth(userRecord.getUserId(),NextDayTimeMillis);
                        boRepositoryUserAuth.updateAuthExpires(userAuth);
                        uaResult = new UserAuthrizationResult(userRecord.getAuthToken(),NextDayTimeMillis);
                    }
                    UserAuth uauth = new UserAuth(userRecord.getUserId(),"Y");
                    boRepositoryUserAuth.updateActive(uauth);
                }
                listResult.add(uaResult);
                rs = new Result("0", "ok", listResult);
            } else {
                rs = new Result("1", "fail:您的密码输入有误，请重新输入！", listResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs = new Result("1", "fail:"+(e.getCause()==null?e.getMessage():e.getCause().toString()), listResult);
        }
        return rs;

    }

    @GET
    @Override
    @JSONP(queryParam="callback")
    @Produces("application/x-javascript;charset=utf-8")
    @Path("/userauthrization")
    public IResult<IUserAuthrizationResult> LoginUser(@QueryParam("companyName")String companyName,@QueryParam("userName")String userName,@QueryParam("password")String password) {
        Result rs = new Result();
        UserAuthrizationResult uaResult = new UserAuthrizationResult();
        List<UserAuthrizationResult> listResult = new ArrayList<UserAuthrizationResult>();
        try {
            //根据公司名称和用户名查询用户信息，并且为密码参数进行MD5加密与用户密码进行比对
            Company company = boRepositoryCompany.serchCompanyId(companyName);
            User loginUser =  boRepositoryUser.getUserByCompanyId(userName,company.getCompanyId());
            String hmacPassword = MD5Util.byteArrayToHexString(MD5Util.encryptHMAC(loginUser.getMobilePassword().getBytes(),"avatech"));
            if (hmacPassword.equals(password)) {
                //用户密码正确，获取截止到登录日期后一天的13位时间戳作为有效期
                long NextDayTimeMillis = Long.parseLong(DataConvert.dateToStamp());
                //查询用户历史登录记录
                UserAuth userRecord = boRepositoryUserAuth.serchLoginRecord(loginUser.getUserName());
                if(userRecord==null) {
                    //没有用户记录则新建
                    String authToken = UUIDUtil.randomUUID32();
                    userRecord = new UserAuth(loginUser.getUserName(), loginUser.getIsMobileUser(), "客户", authToken, NextDayTimeMillis, "Y");
                    boRepositoryUserAuth.saveLoginRecord(userRecord);
                    uaResult = new UserAuthrizationResult(authToken,NextDayTimeMillis);
                }else{
                    //存在用户记录则得到当前登录时间的时间戳，和记录时间戳进行比对，在有效期内则返回，否则更新
                    Long currentTimeMillis = System.currentTimeMillis();
                    if(currentTimeMillis<userRecord.getAuthExpires()){
                        uaResult = new UserAuthrizationResult(userRecord.getAuthToken(),userRecord.getAuthExpires());
                    }else{
                        UserAuth userAuth = new UserAuth(userRecord.getUserId(),NextDayTimeMillis);
                        boRepositoryUserAuth.updateAuthExpires(userAuth);
                        uaResult = new UserAuthrizationResult(userRecord.getAuthToken(),NextDayTimeMillis);
                    }
                    UserAuth uauth = new UserAuth(userRecord.getUserId(),"Y");
                    boRepositoryUserAuth.updateActive(uauth);
                }
                listResult.add(uaResult);
                rs = new Result("0", "ok", listResult);
            } else {
                rs = new Result("1", "fail:您的密码输入有误，请重新输入！", listResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs = new Result("1", "fail:"+(e.getCause()==null?e.getMessage():e.getCause().toString()), listResult);
        }
        return rs;

    }



    @UserRequest
    @DELETE
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/userauthrization")
    //用户退出
    public IResult Logout(@QueryParam(ServicePath.TOKEN_NAMER)String token) {
        Result result = new Result();
            UserAuth auth = boRepositoryUserAuth.serchAuthByToken(token);
        try {
            auth.setIsActive("N");
            boRepositoryUserAuth.updateActive(auth);
            result = new Result("0", "ok", null);
        }catch (Exception e){
            e.printStackTrace();
            result = new Result("0", "failed:"+e.getCause(), null);
        }
        return result;
    }



    @GET
    @Path("/getname")
    @Produces("text/plain")
    public String UserLogin(){
        return "hello";
    }
}
