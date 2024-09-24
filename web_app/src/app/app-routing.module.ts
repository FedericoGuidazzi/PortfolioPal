import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from './utils/auth-guard/auth.guard';
import { loginGuard } from './utils/login-guard/login.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/landing-page/landing-page.component').then(
        (c) => c.LandingPageComponent
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((c) => c.LoginComponent),
    canActivate: [loginGuard],
  },
  {
    path: 'signup',
    loadComponent: () =>
      import('./pages/sign-up/sign-up.component').then(
        (c) => c.SignUpComponent
      ),
    canActivate: [loginGuard],
  },
  {
    path: 'ranking',
    loadComponent: () =>
      import('./pages/ranking/ranking.component').then(
        (c) => c.RankingComponent
      ),
    canActivate: [authGuard],
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(
        (c) => c.DashboardComponent
      ),
    canActivate: [authGuard],
  },
  {
    path: 'dashboard/:portfolioId',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(
        (c) => c.DashboardComponent
      ),
    canActivate: [authGuard],
  },
  {
    path: 'asset/:assetName/:portfolioId',
    loadComponent: () =>
      import('./pages/asset/asset.component').then((c) => c.AssetComponent),
    canActivate: [authGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
