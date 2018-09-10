package org.zstack.test.integration.network.l3network

import org.zstack.header.network.service.NetworkServiceType
import org.zstack.network.securitygroup.SecurityGroupConstant
import org.zstack.network.service.eip.EipConstant
import org.zstack.network.service.flat.FlatNetworkServiceConstant
import org.zstack.network.service.virtualrouter.VirtualRouterConstant
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.Test
import org.zstack.utils.data.SizeUnit

/**
 * Created by camile on 2017/4
 */
class Env {
    def DOC = """

"""



    static EnvSpec OneIpL3Network() {
        return Test.makeEnv {
            instanceOffering {
                name = "instanceOffering"
                memory = SizeUnit.GIGABYTE.toByte(2)
                cpu = 2
            }

            diskOffering {
                name = "diskOffering"
                diskSize = SizeUnit.GIGABYTE.toByte(20)
            }

            sftpBackupStorage {
                name = "sftp"
                url = "/sftp"
                username = "root"
                password = "password"
                hostname = "localhost"

                image {
                    name = "image1"
                    url = "http://zstack.org/download/test.qcow2"
                }

                image {
                    name = "vr"
                    url = "http://zstack.org/download/vr.qcow2"
                }
            }

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        username = "root"
                        password = "password"
                        totalCpu = 40
                        totalMem = SizeUnit.GIGABYTE.toByte(32)

                    }

                    attachPrimaryStorage("local")
                    attachL2Network("l2")
                }

                localPrimaryStorage {
                    name = "local"
                    url = "/local_ps"
                }

                l2NoVlanNetwork {
                    name = "l2"
                    physicalInterface = "eth0"

                    l3Network {
                        name = "l3"

                        ip {
                            startIp = "192.168.100.10"
                            endIp = "192.168.100.10"
                            netmask = "255.255.255.0"
                            gateway = "192.168.100.1"
                        }
                    }

                    l3Network {
                        name = "l3-1"

                        service {
                            provider = VirtualRouterConstant.PROVIDER_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString()]
                        }
                    }

                    l3Network {
                        name = "pubL3"

                        ip {
                            startIp = "12.16.10.10"
                            endIp = "12.16.10.100"
                            netmask = "255.255.255.0"
                            gateway = "12.16.10.1"
                        }
                    }

                }

                virtualRouterOffering {
                    name = "vr"
                    memory = SizeUnit.MEGABYTE.toByte(512)
                    cpu = 2
                    useManagementL3Network("pubL3")
                    usePublicL3Network("pubL3")
                    useImage("vr")
                }

                attachBackupStorage("sftp")
            }
        }
    }

    static EnvSpec Ipv6FlatL3Network() {
        return Test.makeEnv {
            instanceOffering {
                name = "instanceOffering"
                memory = SizeUnit.GIGABYTE.toByte(2)
                cpu = 2
            }

            diskOffering {
                name = "diskOffering"
                diskSize = SizeUnit.GIGABYTE.toByte(20)
            }

            sftpBackupStorage {
                name = "sftp"
                url = "/sftp"
                username = "root"
                password = "password"
                hostname = "localhost"

                image {
                    name = "image1"
                    url = "http://zstack.org/download/test.qcow2"
                }

                image {
                    name = "vr"
                    url = "http://zstack.org/download/vr.qcow2"
                }
            }

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        username = "root"
                        password = "password"
                        totalCpu = 40
                        totalMem = SizeUnit.GIGABYTE.toByte(32)

                    }

                    attachPrimaryStorage("local")
                    attachL2Network("l2")
                }

                localPrimaryStorage {
                    name = "local"
                    url = "/local_ps"
                }

                l2NoVlanNetwork {
                    name = "l2"
                    physicalInterface = "eth0"

                    l3Network {
                        name = "l3-SLAAC"
                        ipVersion = 6

                        ipv6 {
                            name = "ipv6-SLAAC"
                            networkCidr = "2001:2001::/64"
                            addressMode = "SLAAC"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }

                    l3Network {
                        name = "l3-Stateless-DHCP"
                        ipVersion = 6

                        ipv6 {
                            name = "ipv6-Stateless-DHCP"
                            networkCidr = "2001:2002::/64"
                            addressMode = "Stateless-DHCP"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }

                    l3Network {
                        name = "l3-Statefull-DHCP"
                        ipVersion = 6

                        ipv6 {
                            name = "ipv6-Statefull-DHCP"
                            networkCidr = "2001:2003::/64"
                            addressMode = "Stateful-DHCP"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }

                    l3Network {
                        name = "l3-Statefull-DHCP-1"
                        ipVersion = 6
                        category = "Public"

                        ipv6 {
                            name = "ipv6-Statefull-DHCP-1"
                            networkCidr = "2001:2004::/64"
                            addressMode = "Stateful-DHCP"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }

                    l3Network {
                        name = "l3"

                        ip {
                            startIp = "192.168.100.10"
                            endIp = "192.168.100.100"
                            netmask = "255.255.255.0"
                            gateway = "192.168.100.1"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }

                    l3Network {
                        name = "l3-1"
                        category = "Public"

                        ip {
                            startIp = "192.168.101.10"
                            endIp = "192.168.101.100"
                            netmask = "255.255.255.0"
                            gateway = "192.168.101.1"
                        }

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE
                            types = [NetworkServiceType.DHCP.toString(), NetworkServiceType.DNS.toString(), EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        service {
                            provider = SecurityGroupConstant.SECURITY_GROUP_PROVIDER_TYPE
                            types = [SecurityGroupConstant.SECURITY_GROUP_NETWORK_SERVICE_TYPE]
                        }
                    }
                }

                attachBackupStorage("sftp")
            }
        }
    }
}
