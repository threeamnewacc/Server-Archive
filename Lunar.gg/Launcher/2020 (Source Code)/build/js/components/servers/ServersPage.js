"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ServersPage = void 0;
const React = require("react");
const ServerTile_1 = require("./ServerTile");
const SpacerServerTile_1 = require("./SpacerServerTile");
const PAGE_SIZE = 9;
class ServersPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            page: 0,
            maxIndex: Math.ceil(this.props.servers.length / PAGE_SIZE) - 1
        };
    }
    modifyPage(mod) {
        const nextPage = this.state.page + mod;
        if (nextPage < 0 || nextPage > this.state.maxIndex) {
            return;
        }
        this.setState({ page: nextPage });
    }
    render() {
        const page = this.state.page;
        const servers = this.props.servers.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
        const serverTiles = servers.map(s => React.createElement(ServerTile_1.ServerTile, { key: s.name, server: s, onClick: () => this.props.serversOnClick(s) }));
        // add empty spacers
        const spacersNeeded = PAGE_SIZE - serverTiles.length;
        for (let i = 0; i < spacersNeeded; i++) {
            serverTiles.push(React.createElement(SpacerServerTile_1.SpacerServerTile, { key: i }));
        }
        return (React.createElement("div", { id: "servers-container", className: "container-fluid" },
            React.createElement("div", { id: "page-left", className: "page-button " + (this.state.page === 0 ? "disabled" : ""), onClick: () => this.modifyPage(-1) },
                React.createElement("i", { className: "fas fa-chevron-left fa-3x" })),
            React.createElement("div", { className: "row container-fluid", id: "serversContainer" }, serverTiles),
            React.createElement("div", { id: "page-right", className: "page-button " + (this.state.page === this.state.maxIndex ? "disabled" : ""), onClick: () => this.modifyPage(1) },
                React.createElement("i", { className: "fas fa-chevron-right fa-3x" }))));
    }
}
exports.ServersPage = ServersPage;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2VydmVyc1BhZ2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9zZXJ2ZXJzL1NlcnZlcnNQYWdlLnRzeCJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7QUFBQSwrQkFBK0I7QUFFL0IsNkNBQTBDO0FBQzFDLHlEQUFzRDtBQUV0RCxNQUFNLFNBQVMsR0FBRyxDQUFDLENBQUM7QUFZcEIsTUFBYSxXQUFZLFNBQVEsS0FBSyxDQUFDLFNBQXVCO0lBRTFELFlBQVksS0FBWTtRQUNwQixLQUFLLENBQUMsS0FBSyxDQUFDLENBQUM7UUFDYixJQUFJLENBQUMsS0FBSyxHQUFHO1lBQ1QsSUFBSSxFQUFFLENBQUM7WUFDUCxRQUFRLEVBQUUsSUFBSSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLE9BQU8sQ0FBQyxNQUFNLEdBQUcsU0FBUyxDQUFDLEdBQUcsQ0FBQztTQUNqRSxDQUFDO0lBQ04sQ0FBQztJQUVELFVBQVUsQ0FBQyxHQUFXO1FBQ2xCLE1BQU0sUUFBUSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsSUFBSSxHQUFHLEdBQUcsQ0FBQztRQUV2QyxJQUFJLFFBQVEsR0FBRyxDQUFDLElBQUksUUFBUSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxFQUFFO1lBQ2hELE9BQU87U0FDVjtRQUVELElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxJQUFJLEVBQUUsUUFBUSxFQUFFLENBQUMsQ0FBQztJQUN0QyxDQUFDO0lBRUQsTUFBTTtRQUNGLE1BQU0sSUFBSSxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsSUFBSSxDQUFDO1FBQzdCLE1BQU0sT0FBTyxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxJQUFJLEdBQUcsU0FBUyxFQUFFLENBQUMsSUFBSSxHQUFHLENBQUMsQ0FBQyxHQUFHLFNBQVMsQ0FBQyxDQUFDO1FBQ25GLE1BQU0sV0FBVyxHQUFHLE9BQU8sQ0FBQyxHQUFHLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxvQkFBQyx1QkFBVSxJQUFDLEdBQUcsRUFBRSxDQUFDLENBQUMsSUFBSSxFQUFFLE1BQU0sRUFBRSxDQUFDLEVBQUUsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsY0FBYyxDQUFDLENBQUMsQ0FBQyxHQUFJLENBQUMsQ0FBQztRQUUxSCxvQkFBb0I7UUFDcEIsTUFBTSxhQUFhLEdBQUcsU0FBUyxHQUFHLFdBQVcsQ0FBQyxNQUFNLENBQUM7UUFDckQsS0FBSyxJQUFJLENBQUMsR0FBRyxDQUFDLEVBQUUsQ0FBQyxHQUFHLGFBQWEsRUFBRSxDQUFDLEVBQUUsRUFBRTtZQUNwQyxXQUFXLENBQUMsSUFBSSxDQUFDLG9CQUFDLG1DQUFnQixJQUFDLEdBQUcsRUFBRSxDQUFDLEdBQUksQ0FBQyxDQUFDO1NBQ2xEO1FBRUQsT0FBTyxDQUFDLDZCQUFLLEVBQUUsRUFBQyxtQkFBbUIsRUFBQyxTQUFTLEVBQUMsaUJBQWlCO1lBQzNELDZCQUFLLEVBQUUsRUFBQyxXQUFXLEVBQUMsU0FBUyxFQUFFLGNBQWMsR0FBRyxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsSUFBSSxLQUFLLENBQUMsQ0FBQyxDQUFDLENBQUMsVUFBVSxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsRUFBRSxPQUFPLEVBQUUsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsQ0FBQztnQkFDekgsMkJBQUcsU0FBUyxFQUFDLDJCQUEyQixHQUFHLENBQ3pDO1lBQ04sNkJBQUssU0FBUyxFQUFDLHFCQUFxQixFQUFDLEVBQUUsRUFBQyxrQkFBa0IsSUFDckQsV0FBVyxDQUNWO1lBQ04sNkJBQUssRUFBRSxFQUFDLFlBQVksRUFBQyxTQUFTLEVBQUUsY0FBYyxHQUFHLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxJQUFJLEtBQUssSUFBSSxDQUFDLEtBQUssQ0FBQyxRQUFRLENBQUMsQ0FBQyxDQUFDLFVBQVUsQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFDLEVBQUUsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxVQUFVLENBQUMsQ0FBQyxDQUFDO2dCQUMzSSwyQkFBRyxTQUFTLEVBQUMsNEJBQTRCLEdBQUcsQ0FDMUMsQ0FDSixDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUE1Q0Qsa0NBNENDIn0=