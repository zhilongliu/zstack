package org.zstack.compute.vm;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusCallBack;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.Q;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.core.workflow.Flow;
import org.zstack.header.core.workflow.FlowRollback;
import org.zstack.header.core.workflow.FlowTrigger;
import org.zstack.header.core.workflow.NoRollbackFlow;
import org.zstack.header.message.MessageReply;
import org.zstack.header.network.l3.*;
import org.zstack.header.vm.*;
import org.zstack.network.l3.L3NetworkManager;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.network.IPv6Constants;

import java.util.Map;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VmChangeDefaultL3NetworkFlow extends NoRollbackFlow {
    private static final CLogger logger = Utils.getLogger(VmChangeDefaultL3NetworkFlow.class);
    @Autowired
    protected DatabaseFacade dbf;
    @Autowired
    protected CloudBus bus;
    @Autowired
    protected ErrorFacade errf;
    @Autowired
    protected L3NetworkManager l3nm;
    @Autowired
    private PluginRegistry pluginRgty;

    @Override
    public void run(final FlowTrigger trigger, final Map data) {
        final VmNicInventory nic = (VmNicInventory) data.get(VmInstanceConstant.Params.VmNicInventory.toString());
        final UsedIpInventory usedIp = (UsedIpInventory) data.get(VmInstanceConstant.Params.UsedIPInventory.toString());

        if (nic.getVmInstanceUuid() == null) {
            trigger.next();
            return;
        }

        VmInstanceVO vmVo = dbf.findByUuid(nic.getVmInstanceUuid(), VmInstanceVO.class);
        if (vmVo.getDefaultL3NetworkUuid() != null && !vmVo.getDefaultL3NetworkUuid().equals(usedIp.getL3NetworkUuid())) {
            trigger.next();
            return;
        }

        vmVo.setDefaultL3NetworkUuid(nic.getL3NetworkUuid());
        dbf.updateAndRefresh(vmVo);
        trigger.next();
    }
}
