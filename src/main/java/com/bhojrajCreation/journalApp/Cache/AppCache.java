package com.bhojrajCreation.journalApp.Cache;

import com.bhojrajCreation.journalApp.Entity.ConfigJournalApp;
import com.bhojrajCreation.journalApp.Repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {
    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String,String> App_Cache ;

    @PostConstruct
    public void init(){
        App_Cache = new HashMap<>();
        List<ConfigJournalApp> all = configJournalAppRepository.findAll();
        for(ConfigJournalApp configJournalApp : all){
            App_Cache.put(configJournalApp.getKey(),configJournalApp.getValue());
        }

    }
}
