/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.module.RouterBase;

import java.util.ArrayList;


/**
 * Created by ygc on 14-10-20.
 */
public class MainRouter {
    private static MainRouter singleton = null;
    private Context context;

    public MainRouter(Context context) {
        this.context = context;
    }

    public static MainRouter getInstance(Context context) {
        synchronized (MainRouter.class) {
            if (singleton == null) {
                singleton = new MainRouter(context.getApplicationContext());
            }
        }

        return singleton;
    }

    public boolean showActivity(String module, int id, Bundle bundle) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        if (module != null) {
            for (ModBase mod : mods) {
                if (mod.getName().equals(module)) {
                    RouterBase router = mod.getRouter(context);
                    if (router != null) {
                        if (bundle == null) {
                            return router.showActivity(id);
                        } else {
                            return router.showActivity(id, bundle);
                        }
                    }
                }
            }
        } else {
            for (ModBase mod : mods) {
                RouterBase router = mod.getRouter(context);
                if (router != null) {
                    if (router.showActivity(id, bundle)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean showActivity(int id) {
        return showActivity(null, id, null);
    }

    public boolean showActivity(int id, Bundle bundle) {
        return showActivity(null, id, bundle);
    }

    // Show activity with flags
    public boolean showActivity(int id, int flags) {
        return showActivity(null, id, null, flags);
    }

    public boolean showActivity(String module, int id, Bundle bundle, int flags) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        if (module != null) {
            for (ModBase mod : mods) {
                if (mod.getName().equals(module)) {
                    RouterBase router = mod.getRouter(context);
                    if (router != null) {
                        if (bundle != null) {
                            return router.showActivity(id, bundle, flags);
                        } else {
                            return router.showActivity(id, flags);
                        }
                    }
                }
            }
        } else {
            for (ModBase mod : mods) {
                RouterBase router = mod.getRouter(context);
                if (router != null) {
                    if (router.showActivity(id, bundle, flags)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean showActivity(String module, int id) {
        return showActivity(module, id, null);
    }

    public boolean showActivityForResult(String module, Activity parentActivity, int id, int requestCode, Bundle param) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        if (module != null) {
            for (ModBase mod : mods) {
                if (mod.getName().equals(module)) {
                    RouterBase router = mod.getRouter(context);
                    if (router != null) {
                        return router.showActivityForResult(parentActivity, id, requestCode, param);
                    }
                }
            }
        } else {
            return showActivityForResult(parentActivity, id, requestCode, param);
        }
        return false;
    }

    public boolean showActivityForResult(Activity parentActivity, int id, int requestCode, Bundle param) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        for (ModBase mod : mods) {
            RouterBase router = mod.getRouter(context);
            if (router != null) {
                return router.showActivityForResult(parentActivity, id, requestCode, param);
            }
        }

        return false;
    }

    public boolean showActivityForResult(Fragment fragment, int id, int requestCode, Bundle param) {
        if (fragment == null) return false;
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        for (ModBase mod : mods) {
            RouterBase router = mod.getRouter(context);
            if (router != null) {
                return router.showActivityForResult(fragment, id, requestCode, param);
            }
        }

        return false;
    }


    //String

    public boolean showActivity(String id) {
        return showActivity(null, id, null);
    }


    public boolean showActivity(String module, String id, Bundle bundle) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        ArrayList<ModBase> mods = appContext.getMods();
        if (module != null) {
            for (ModBase mod : mods) {
                if (mod.getName().equals(module)) {
                    RouterBase router = mod.getRouter(context);
                    if (router != null) {
                        if (bundle == null) {
                            return router.showActivity(id);
                        } else {
                            return router.showActivity(id, bundle);
                        }
                    }
                }
            }
        } else {
            for (ModBase mod : mods) {
                RouterBase router = mod.getRouter(context);
                if (router != null) {
                    if (router.showActivity(id, bundle)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

