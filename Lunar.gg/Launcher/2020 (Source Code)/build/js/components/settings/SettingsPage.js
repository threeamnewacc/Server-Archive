"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SettingsPage = void 0;
const React = require("react");
const os_1 = require("os");
const AfterLaunchActionSetting_1 = require("./AfterLaunchActionSetting");
const AllocatedMemorySetting_1 = require("./AllocatedMemorySetting");
const LaunchDirectorySetting_1 = require("./LaunchDirectorySetting");
const ResolutionSetting_1 = require("./ResolutionSetting");
const ExperimentalUpdatesSetting_1 = require("./ExperimentalUpdatesSetting");
const RestoreToDefaultsButton_1 = require("./RestoreToDefaultsButton");
class SettingsPage extends React.Component {
    render() {
        let systemMemoryMb = os_1.totalmem() / 1024 / 1024;
        return (React.createElement("div", { id: "settings-container", className: "container-fluid" },
            React.createElement("div", { className: "row" },
                React.createElement("div", { id: "client-settings", className: "col-12" },
                    React.createElement("div", { className: "card" },
                        React.createElement("div", { id: "client-settings-title", className: "card-body lunar-text card-img-top" },
                            React.createElement("h1", null,
                                React.createElement("i", { className: "fas fa-cogs mr-1" }),
                                "CLIENT SETTINGS"),
                            React.createElement("h6", null, "MEMORY ALLOCATION & LAUNCHER PREFERENCES")),
                        React.createElement("div", { id: "client-settings-content", className: "card-body" },
                            React.createElement("div", null,
                                React.createElement("div", { id: "left-client-section", className: "col-8" },
                                    React.createElement("div", { className: "col-6 mb-3" },
                                        React.createElement(AfterLaunchActionSetting_1.AfterLaunchActionSetting, null)),
                                    React.createElement("div", { className: "col-6 mb-3" },
                                        React.createElement(AllocatedMemorySetting_1.AllocatedMemorySetting, { minMemoryMb: 512, maxMemoryMb: systemMemoryMb })),
                                    React.createElement("div", { className: "col-6" },
                                        React.createElement(LaunchDirectorySetting_1.LaunchDirectorySetting, null)),
                                    React.createElement("div", { className: "col-6" },
                                        React.createElement(ResolutionSetting_1.ResolutionSetting, null))),
                                React.createElement("div", { className: "col-4" },
                                    React.createElement(ExperimentalUpdatesSetting_1.ExperimentalUpdatesSetting, null)),
                                React.createElement("div", { className: "col-12" },
                                    React.createElement(RestoreToDefaultsButton_1.RestoreToDefaultsButton, null)))))))));
    }
}
exports.SettingsPage = SettingsPage;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2V0dGluZ3NQYWdlLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvc2V0dGluZ3MvU2V0dGluZ3NQYWdlLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFDL0IsMkJBQThCO0FBQzlCLHlFQUFzRTtBQUN0RSxxRUFBa0U7QUFDbEUscUVBQWtFO0FBQ2xFLDJEQUF3RDtBQUN4RCw2RUFBMEU7QUFDMUUsdUVBQW9FO0FBRXBFLE1BQWEsWUFBYSxTQUFRLEtBQUssQ0FBQyxTQUFpQjtJQUVyRCxNQUFNO1FBQ0YsSUFBSSxjQUFjLEdBQUcsYUFBUSxFQUFFLEdBQUcsSUFBSSxHQUFHLElBQUksQ0FBQztRQUU5QyxPQUFPLENBQUMsNkJBQUssRUFBRSxFQUFDLG9CQUFvQixFQUFDLFNBQVMsRUFBQyxpQkFBaUI7WUFDNUQsNkJBQUssU0FBUyxFQUFDLEtBQUs7Z0JBQ2hCLDZCQUFLLEVBQUUsRUFBQyxpQkFBaUIsRUFBQyxTQUFTLEVBQUMsUUFBUTtvQkFDeEMsNkJBQUssU0FBUyxFQUFDLE1BQU07d0JBQ2pCLDZCQUFLLEVBQUUsRUFBQyx1QkFBdUIsRUFBQyxTQUFTLEVBQUMsbUNBQW1DOzRCQUN6RTtnQ0FDSSwyQkFBRyxTQUFTLEVBQUMsa0JBQWtCLEdBQUc7a0RBRWpDOzRCQUNMLDJFQUFpRCxDQUMvQzt3QkFDTiw2QkFBSyxFQUFFLEVBQUMseUJBQXlCLEVBQUMsU0FBUyxFQUFDLFdBQVc7NEJBQ25EO2dDQUNJLDZCQUFLLEVBQUUsRUFBQyxxQkFBcUIsRUFBQyxTQUFTLEVBQUMsT0FBTztvQ0FDM0MsNkJBQUssU0FBUyxFQUFDLFlBQVk7d0NBQ3ZCLG9CQUFDLG1EQUF3QixPQUFHLENBQzFCO29DQUNOLDZCQUFLLFNBQVMsRUFBQyxZQUFZO3dDQUN2QixvQkFBQywrQ0FBc0IsSUFBQyxXQUFXLEVBQUUsR0FBRyxFQUFFLFdBQVcsRUFBRSxjQUFjLEdBQUksQ0FDdkU7b0NBQ04sNkJBQUssU0FBUyxFQUFDLE9BQU87d0NBQ2xCLG9CQUFDLCtDQUFzQixPQUFHLENBQ3hCO29DQUNOLDZCQUFLLFNBQVMsRUFBQyxPQUFPO3dDQUNsQixvQkFBQyxxQ0FBaUIsT0FBRyxDQUNuQixDQUNKO2dDQUNOLDZCQUFLLFNBQVMsRUFBQyxPQUFPO29DQUNsQixvQkFBQyx1REFBMEIsT0FBRyxDQUM1QjtnQ0FDTiw2QkFBSyxTQUFTLEVBQUMsUUFBUTtvQ0FDbkIsb0JBQUMsaURBQXVCLE9BQUcsQ0FDekIsQ0FDSixDQUNKLENBQ0osQ0FDSixDQUNKLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBOUNELG9DQThDQyJ9