package com.example.asset.services;

import com.example.asset.models.Asset;
import com.example.asset.models.bin.GetAssetBin;

public interface GetAssetService {
    Asset getAsset(GetAssetBin getAssetBin);
}
