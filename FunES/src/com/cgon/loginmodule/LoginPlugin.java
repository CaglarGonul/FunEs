package com.cgon.loginmodule;

import com.cgon.hqls.SelectHQLS;
import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import game_db.entity.Userlogin;
import game_db.util.HibernateUtil;
import java.util.concurrent.Executor;
import org.hibernate.Session;

public class LoginPlugin extends BasePlugin{
    
    private Executor _executor;
    
    private void Log(String logMessage){
        getApi().getLogger().debug(logMessage);
    }
    
    protected final void execute( Runnable command ) {
        if ( null == _executor ) {
            throw new IllegalStateException( "executor not initialized" );
        }
        _executor.execute( command );
    }
    
    @Override
    public void init(EsObjectRO parameters){
        Log("LoginPlugin is started...");
        _executor = (Executor) getApi().acquireManagedObject( "Executor", null );
    }
       
    public void handleLoginRequest(String userName, String password){
        final String  userNameF = userName;
        final String passwordF = password;
        execute(new Runnable() {
            @Override
                public void run() {
                asynchCheckUserForLogin(userNameF, passwordF);
            }
         });            
    }
    
    private void asynchCheckUserForLogin(final String userName, final String password){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Userlogin fetchedUser = SelectHQLS.getUserByUname(userName, session);

        if(fetchedUser!=null && fetchedUser.getPassword().equals(password)){
            boolean loginState = true;
            sendLoginState(loginState, userName);
            Log("LOGIN PASSED... " + userName);
        }
        else{
            boolean loginState = false;
            sendLoginState(loginState, userName);
            Log("LOGIN FAILED... " + userName);

            getApi().kickUserFromServer(userName, null);
        }
        session.getTransaction().commit();
        session.close();
    }

    private void sendLoginState(boolean loginState, final String userName) {
        EsObject loginStateEsob = new EsObject();
        loginStateEsob.setString("ACTION", "LOGIN_CHECKED");
        loginStateEsob.setBoolean("LOGIN_PASSED", loginState);
        sendToOne(userName, loginStateEsob);
    }
    
    private void sendToOne(String playerName, EsObject message){
        getApi().sendPluginMessageToUser(playerName, message);
    }
}
