/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.core;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author Heiko Braun
 * @author David Bosschaert
 * @date 5/2/11
 */
public interface UIMessages extends Messages {

    String changeServerStatus(String state, String name);

    String deleteServerConfig();

    String deleteServerConfigConfirm(String name);

    String deploymentsFor(String serverGroup);

    String common_validation_portOffsetUndefined(String errMessage);

    String common_validation_notEmptyNoSpace();

    String deleteServerGroupConfirm(String groupName);

    String deleteServerGroup();

    String deleteJVM();

    String deleteJVMConfirm();

    String removeProperty();

    String removePropertyConfirm(String key);

    String common_validation_requiredField();

    String common_validation_heapSize();

    String mustBeDeployableArchive(String fieldName);

    String restartRequired();

    String alreadyExists(String fieldName);

    String commmon_description_newServerGroup();

    String savedSettings();

    String restartRequiredConfirm();

    String removeFromConfirm(String entity, String target);

    String failedToRemoveFrom(String entity, String target);

    String removedFrom(String entity, String target);

    String enableConfirm(String entity);

    String disableConfirm(String entity);

    String failedToEnable(String entity);

    String failedToDisable(String entity);

    String successEnabled(String entity);

    String successDisabled(String entity);

    String addConfirm(String entity, String target);

    String failedToAdd(String entity, String target);

    String successAdd(String entity, String target);

    String alreadyAssignedTo(String deploymentName, String serverGroup);

    String subsys_naming_failedToLoadJNDIView();

    String subsys_configadmin_addNoPIDselected();

    String subsys_configadmin_remove();

    String subsys_configadmin_removeConfirm(String pid);

    String subsys_configadmin_oneValueRequired();

    String subsys_configadmin_removeOnLastValueConfirm(String pid);

    String subsys_osgi_frameworkPropertiesHelp();

    String subsys_osgi_capabilitiesHelp();

    String subsys_osgi_removeCapability();

    String subsys_osgi_removeCapabilityConfirm(String id);

    String subsys_osgi_cant_start_fragment(String bsn);

    String subsys_osgi_cant_stop_fragment(String bsn);

    String subsys_osgi_activate();

    String subsys_osgi_activating();

    String subsys_messaging(String providerName);

    String temporarilyUnavailable();

    String deleteTitle(String name);

    String deleteConfirm(String name);

    String createTitle(String itemName);

    String added(String name);

    String addingFailed(String name);

    String deleted(String name);

    String deletionFailed(String name);

    String saved(String name);

    String saveFailed(String name);

    String modify(String name);

    String modifyConfirm(String name);

    String modified(String name);

    String modificationFailed(String name);

    String serversRunningOnHost(String hostName);

    String selectServerGroups();

    String selectServerGroupsFor(String deploymentRuntimeName);

    String alreadyAssignedToAllGroups(String deploymentRuntimeName);

    String noServerGroupsSelected();

    String unknown_error();

    String available(String s);

    String successful(String p0);

    String failed(String p0);

    String server_config_stillRunning(String name);

    String subsys_jca_err_ds_notEnabled(String name);

    String subsys_jpa_err_mericDisabled(String s);

    String server_reload_confirm(String p0);

    String subsys_jca_err_ds_enabled(String name);

    String pleaseChoseanItem();

    String path_description();

    String topology_description();

    String topology_no_server();

    String extensions_description();

    String deployment_repo_description();

    String deployment_filesystem();

    String deployments_for_group();

    String environment_description();

    String transaction_log_description();

    String administration_scoped_role_in_use(int usage);

    String administration_members(String name);

    SafeHtml access_control_provider();

    String deployment_assign_help();

    String no_groups_header();

    String no_groups_warning();

    String verify_datasource_successful_message(String datasource);

    String verify_datasource_failed_message(String datasource);

    String patch_manager_rollback_body(String id);

    String patch_manager_stop_server_body(String host);

    String patch_manager_applying(String filename);

    String patch_manager_restart_yes(String serverOrHost);

    SafeHtml patch_manager_conflict_override_title();

    SafeHtml patch_manager_desc_product();

    String patch_manager_applying_patch_body(String filename);

    String patch_manager_restart_needed(String serverOrHost);

    String patch_manager_error_parse_result(String exception, String payload);

    String patch_manager_rolling_back_body(String id);

    SafeHtml content_box_create_datasource_body_standalone();

    SafeHtml content_box_create_datasource_body_domain();

    SafeHtml content_box_new_deployment_body_standalone();

    SafeHtml content_box_new_deployment_body_domain();

    SafeHtml content_box_apply_patch_body_standalone();

    SafeHtml content_box_apply_patch_body_domain();

    SafeHtml content_box_role_assignment_body();

    SafeHtml content_box_create_server_group_body();

    SafeHtml content_box_topology_body();

    SafeHtml search_index_pending();

    String patch_manager_restart_verify(String host);
}
