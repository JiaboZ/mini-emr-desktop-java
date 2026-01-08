package com.miniehr.dao;

import com.miniehr.model.ProviderLite;

import java.util.List;

public interface ProviderDao {
    List<ProviderLite> listAll();
}
