using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.WindowsAzure;
using Microsoft.WindowsAzure.Diagnostics;
using Microsoft.WindowsAzure.ServiceRuntime;
using Microsoft.Web.Administration;

namespace WebRole1
{
    public class WebRole : RoleEntryPoint
    {
        private void ConfigureAppPools()
        {
            using (ServerManager serverManager = new ServerManager())
            {
                foreach (var appPool in serverManager.ApplicationPools)
                {
                    appPool.ProcessModel.IdleTimeout = TimeSpan.Zero;
                    appPool.Recycling.PeriodicRestart.Time = TimeSpan.Zero;
                }
                serverManager.CommitChanges();
            }
        }

        public override bool OnStart()
        {
            // For information on handling configuration changes
            // see the MSDN topic at http://go.microsoft.com/fwlink/?LinkId=166357.

            ConfigureAppPools();

            return base.OnStart();
        }
    }
}
