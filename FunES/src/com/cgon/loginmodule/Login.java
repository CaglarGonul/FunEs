package com.cgon.loginmodule;

import com.electrotank.electroserver5.extensions.BaseLoginEventHandler;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.LoginContext;
import com.electrotank.electroserver5.extensions.api.UserServerVariableResponse;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;


public class Login extends BaseLoginEventHandler{
    
    private LoginPlugin _loginPlugin;
    
    protected final LoginPlugin getLoginPlugin(){
        if(null==_loginPlugin){
            _loginPlugin = (LoginPlugin)getApi().getServerPlugin("LoginPlugin");
        }
        return _loginPlugin;
    }
    
    private void Log(String logMessage){
        getApi().getLogger().debug(logMessage);
    }  
    
    @Override
    public void init(EsObjectRO parameters){
        Log("LOGIN EVENT HANDLER STARTED...");
    }
    
    @Override
    public ChainAction executeLogin(LoginContext context){
        String userName = context.getUserName();
        String passWord = context.getPassword();
        
        if("".equals(userName) || "".equals(passWord)){
            return ChainAction.Fail;
        }
        setUserServerVariable(userName, passWord, context);
        return ChainAction.OkAndContinue;
    }
    
    @Override
    public void userDidLogin(String userName){
        UserServerVariableResponse uvr = getApi().getUserServerVariable(userName, "LOGIN_INFO");
        if(null!=uvr && null != uvr.getValue()){
            String userNameUvr = uvr.getValue().getString("USERNAME");
            String passwordUvr = uvr.getValue().getString("PASSWORD");
            getLoginPlugin().handleLoginRequest(userNameUvr, passwordUvr);
        }else{
            getApi().kickUserFromServer(userName, null);
        }
    }
    
    public void setUserServerVariable(String userName, String password, LoginContext context) {
        EsObject obj = new EsObject();
        obj.setString("USERNAME", userName);
        obj.setString("PASSWORD", password);
        
        context.addUserServerVariable("LOGIN_INFO", obj);
    }
}
