<?xml version="1.0" encoding="utf-8"?>
<serviceModel xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="MyRESTService.Azure2" generation="1" functional="0" release="0" Id="aecc0143-4fda-42f5-8e2b-237ba708b145" dslVersion="1.2.0.0" xmlns="http://schemas.microsoft.com/dsltools/RDSM">
  <groups>
    <group name="MyRESTService.Azure2Group" generation="1" functional="0" release="0">
      <componentports>
        <inPort name="MyRESTService:Endpoint1" protocol="http">
          <inToChannel>
            <lBChannelMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/LB:MyRESTService:Endpoint1" />
          </inToChannel>
        </inPort>
      </componentports>
      <settings>
        <aCS name="MyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="">
          <maps>
            <mapMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MapMyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </maps>
        </aCS>
        <aCS name="MyRESTServiceInstances" defaultValue="[1,1,1]">
          <maps>
            <mapMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MapMyRESTServiceInstances" />
          </maps>
        </aCS>
      </settings>
      <channels>
        <lBChannel name="LB:MyRESTService:Endpoint1">
          <toPorts>
            <inPortMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTService/Endpoint1" />
          </toPorts>
        </lBChannel>
      </channels>
      <maps>
        <map name="MapMyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" kind="Identity">
          <setting>
            <aCSMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTService/Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </setting>
        </map>
        <map name="MapMyRESTServiceInstances" kind="Identity">
          <setting>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTServiceInstances" />
          </setting>
        </map>
      </maps>
      <components>
        <groupHascomponents>
          <role name="MyRESTService" generation="1" functional="0" release="0" software="C:\Users\brand\Desktop\Tuber\Server\MyRESTService\MyRESTService.Azure2\csx\Release\roles\MyRESTService" entryPoint="base\x64\WaHostBootstrapper.exe" parameters="base\x64\WaIISHost.exe " memIndex="-1" hostingEnvironment="frontendadmin" hostingEnvironmentVersion="2">
            <componentports>
              <inPort name="Endpoint1" protocol="http" portRanges="80" />
            </componentports>
            <settings>
              <aCS name="Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="" />
              <aCS name="__ModelData" defaultValue="&lt;m role=&quot;MyRESTService&quot; xmlns=&quot;urn:azure:m:v1&quot;&gt;&lt;r name=&quot;MyRESTService&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;/m&gt;" />
            </settings>
            <resourcereferences>
              <resourceReference name="DiagnosticStore" defaultAmount="[4096,4096,4096]" defaultSticky="true" kind="Directory" />
              <resourceReference name="EventStore" defaultAmount="[1000,1000,1000]" defaultSticky="false" kind="LogStore" />
            </resourcereferences>
          </role>
          <sCSPolicy>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTServiceInstances" />
            <sCSPolicyUpdateDomainMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTServiceUpgradeDomains" />
            <sCSPolicyFaultDomainMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTServiceFaultDomains" />
          </sCSPolicy>
        </groupHascomponents>
      </components>
      <sCSPolicy>
        <sCSPolicyUpdateDomain name="MyRESTServiceUpgradeDomains" defaultPolicy="[5,5,5]" />
        <sCSPolicyFaultDomain name="MyRESTServiceFaultDomains" defaultPolicy="[2,2,2]" />
        <sCSPolicyID name="MyRESTServiceInstances" defaultPolicy="[1,1,1]" />
      </sCSPolicy>
    </group>
  </groups>
  <implements>
    <implementation Id="6df14ce6-fdf3-43a5-b162-5659437a6b34" ref="Microsoft.RedDog.Contract\ServiceContract\MyRESTService.Azure2Contract@ServiceDefinition">
      <interfacereferences>
        <interfaceReference Id="9e27ded6-4cb1-4b27-a9bd-85d3b475097e" ref="Microsoft.RedDog.Contract\Interface\MyRESTService:Endpoint1@ServiceDefinition">
          <inPort>
            <inPortMoniker name="/MyRESTService.Azure2/MyRESTService.Azure2Group/MyRESTService:Endpoint1" />
          </inPort>
        </interfaceReference>
      </interfacereferences>
    </implementation>
  </implements>
</serviceModel>