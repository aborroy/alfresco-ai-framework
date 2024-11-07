import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, PRIMARY_OUTLET, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import {
  AppConfigService,
  AuthenticationService,
  BasicAlfrescoAuthService,
  StorageService,
  OidcAuthenticationService,
  OauthConfigModel,
  AppConfigValues
} from '@alfresco/adf-core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {
  private authenticationService = inject(AuthenticationService);
  private basicAlfrescoAuthService = inject(BasicAlfrescoAuthService);
  private oidcAuthenticationService = inject(OidcAuthenticationService);
  private router = inject(Router);
  private appConfigService = inject(AppConfigService);
  private dialog = inject(MatDialog);
  private storageService = inject(StorageService);

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const isLoginFragmentPresent = !!this.storageService.getItem('loginFragment');

    if (this.authenticationService.isLoggedIn() && this.authenticationService.isOauth() && isLoginFragmentPresent) {
      return this.redirectByLoginFragment(state);
    }

    const isKerberos = this.appConfigService.get<boolean>('auth.withCredentials', false);
    if (this.authenticationService.isEcmLoggedIn() || isKerberos) {
      return true;
    }

    return this.redirectToUrl(state);
  }

  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.canActivate(route, state);
  }

  // TODO: needs testing with SSO
  private async redirectByLoginFragment(state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    const redirectFragment = this.storageService.getItem('loginFragment');

    if (redirectFragment && this.getLoginRoute(state) !== redirectFragment) {
      await this.navigate(redirectFragment);
      this.storageService.removeItem('loginFragment');
      return false;
    }

    return true;
  }

  // TODO: needs testing with SSO
  private async redirectToUrl(state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    let urlToRedirect = this.getLoginRoute(state);

    const oAuthConfig = this.appConfigService.get<OauthConfigModel>(AppConfigValues.OAUTHCONFIG);

    if (!this.authenticationService.isOauth()) {
      const provider = this.appConfigService.get<string>(AppConfigValues.PROVIDERS, 'ALL');
      this.basicAlfrescoAuthService.setRedirect({
        provider,
        url: state.url
      });

      urlToRedirect = `${urlToRedirect}?redirectUrl=${state.url}`;
      return this.navigate(urlToRedirect);
    } else if (oAuthConfig.silentLogin && !this.oidcAuthenticationService.isPublicUrl()) {
      if (!this.oidcAuthenticationService.hasValidIdToken() || !this.oidcAuthenticationService.hasValidAccessToken()) {
        this.oidcAuthenticationService.ssoLogin(state.url);
      }
    } else {
      return this.navigate(urlToRedirect);
    }

    return false;
  }

  // TODO: needs testing with SSO
  private async navigate(url: string): Promise<boolean> {
    this.dialog.closeAll();
    await this.router.navigateByUrl(this.router.parseUrl(url));
    return false;
  }

  private getLoginRoute(state: RouterStateSnapshot): string {
    const urlTree = this.router.parseUrl(state.url);
    const urlSegmentGroup = urlTree.root.children[PRIMARY_OUTLET];

    if (urlSegmentGroup && urlSegmentGroup.segments.length > 0) {
      return `${urlSegmentGroup.segments[0].path}/login`;
    }

    return `/login`;
  }
}
