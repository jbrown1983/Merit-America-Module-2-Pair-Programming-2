package com.techelevator.dao;

import com.techelevator.model.Site;

import java.util.List;

public interface SiteDao {

    List<Site> getSitesThatAllowRVs(int parkId);

    List<Site> getAvailableSites(int parkId);

}
