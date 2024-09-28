import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AssetService {
  private assetUrl = `${environment.backendUrl}/asset`;

  constructor(private http: HttpClient) {}

  getAssetData(assetId: string, duration: string) {
    return this.http.get(
      this.assetUrl + '/' + assetId + '?duration=' + duration
    );
  }

  searchAssets(asset: string) {
    return this.http.get(this.assetUrl + '/search/' + asset);
  }
}
