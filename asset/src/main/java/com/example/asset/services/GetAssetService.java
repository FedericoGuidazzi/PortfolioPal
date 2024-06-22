package com.example.asset.services;

import com.example.asset.models.Asset;
import com.example.asset.models.bin.GetAssetBin;

import java.util.List;

public interface GetAssetService {
    Asset getAsset(GetAssetBin getAssetBin);

    List<String> getAssetsMatching(String search);
}
