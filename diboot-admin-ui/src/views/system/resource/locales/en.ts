import type { Locale } from './zhCN'
const en: Locale = {
  resource: {
    newButtonPermission: 'New Button Permission',
    parentId: 'Parent Directory',
    parentId0: 'Top-Level Directory',
    displayType: 'Menu Category',
    displayName: 'Menu Name',
    routeMetaIcon: 'Menu Icon',
    routePath: 'Route Path',
    resourceCode: 'Route Name',
    redirectPath: 'Redirect',
    routeMetaUrl: 'External Link',
    appModule: 'Application Module',
    permissionCodes: 'Menu Permissions',
    otherConfig: 'Other Config',
    status: 'Available',
    hidden: 'Hidden',
    keepAlive: 'Cache',
    ignoreAuth: 'Ignore Authentication',
    buttonPermission: 'Button Permission Configuration',
    buttonPermissionConfig: {
      resourceCode: 'Button Permission Code',
      displayName: 'Button Permission Name',
      permissionCodes: 'Button Permission API'
    },
    displayTypeOptions: {
      catalogue: 'Catalogue',
      menu: 'Menu',
      outsideUrl: 'External URL'
    },
    otherConfigDesc: {
      status: 'Available: Controls whether the menu is effective;',
      hidden: 'Hidden: The menu bar will not be displayed when hidden, but the address can still be accessed;',
      keepAlive: 'Cache: Enables keepAlive on the page, caching the current page;',
      ignoreAuth: 'Ignore Authentication: Current page access does not require authentication.'
    },
    placeholder: {
      displayName: 'Enter menu name',
      routePath: 'Enter route address (e.g., route-path)',
      resourceCode: 'Enter route name (e.g., RouteName)',
      redirectPath: 'Enter redirect',
      routeMetaUrl: 'Enter external link',
      permissionCodes: 'Click to focus then select from the permission list',
      componentName: 'Please select the component',
      buttonPermissionConfig: {
        _customCode: 'Enter button permission code',
        resourceCode: 'Please select the current button permission code',
        displayName: 'Enter button permission name'
      }
    },
    selectMenuAndOperate: 'Select the left-side menu and perform operations',
    menuConfig: 'Menu Configuration',
    switchButton: 'Input/select to toggle',
    addChild: 'Add child menu',
    componentNameChange: 'Component name has changed. To avoid cache issues, please select again!',
    componentNonExist: 'Component does not exist and cannot load the menu. Please select again!',
    permissionSelect: {
      title: 'Menu Category Can Configure Permission API',
      permissionApi: 'Configure Permission API',
      searchPlaceholder: 'Search for required APIs: Supports fuzzy search by title, permission code, and API address',
      tip: 'After selecting the application module, configure permissions'
    }
  }
}

export default en
