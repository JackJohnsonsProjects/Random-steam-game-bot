/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstdiscordbot;

import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author Jack
 */
public class BotMain {
    
    public static final IDiscordClient bot = createClient("MzQ4MTA5NDUyMDQzNDg1MTg1.DHiJ-Q.ZE9v5h2qa1bmodjYETUik_Bdg24", true);
    
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\Jack\\Documents"
                + "\\Programming\\Java\\ChromeDriver\\chromedriver.exe");
        EventDispatcher dispatcher = bot.getDispatcher();
        dispatcher.registerListener(new EventListener());
    }
    
    public static IDiscordClient createClient(String token, boolean login) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        } catch (DiscordException e) {
            return null;
        }
        
        
    }
     
}
