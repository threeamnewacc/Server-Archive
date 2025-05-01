"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ServerTile = void 0;
const React = require("react");
const pinger = require('../../lib/minecraft-pinger');
class ServerTile extends React.Component {
    constructor(props) {
        super(props);
        this.state = { playersOnline: 0 };
    }
    componentDidMount() {
        this.intervalId = setInterval(() => this.tick(), 60 * 1000); // every minute
        this.tick(); // run at the start so they don't see 0
    }
    componentWillUnmount() {
        clearInterval(this.intervalId);
    }
    tick() {
        pinger.ping(this.props.server.pingIp, 25565, (error, result) => {
            let players = 0;
            if (error) {
                console.log('Could not ping ' + this.props.server.pingIp + ': ' + error);
            }
            else {
                players = result.players.online;
            }
            this.setState({ playersOnline: players });
        });
    }
    render() {
        return (React.createElement("div", { className: "col-4" },
            React.createElement("div", { className: "card bg-dark server-card" },
                React.createElement("div", { className: "card-body py-4 server-body", style: { backgroundImage: "url('" + this.props.server.background + "')" } },
                    React.createElement("div", { className: "server-inner" },
                        React.createElement("div", { className: "logo-holder" },
                            React.createElement("img", { draggable: false, src: this.props.server.icon, alt: "Logo" })),
                        React.createElement("div", { className: "server-information lunar-text" },
                            React.createElement("h5", { className: "card-title" }, this.props.server.name),
                            React.createElement("p", { className: "card-text" },
                                "Players Online: ",
                                React.createElement("span", null, this.state.playersOnline.toLocaleString())),
                            React.createElement("p", { className: "card-text" },
                                "IP: ",
                                React.createElement("span", null, this.props.server.ip)),
                            React.createElement("p", { className: "card-text" },
                                "Region: ",
                                React.createElement("span", null, this.props.server.region)),
                            React.createElement("p", { className: "card-text" },
                                "Games: ",
                                React.createElement("span", null, this.props.server.gameMode))),
                        React.createElement("div", { className: "server-play lunar-text clickable", onClick: () => this.props.onClick() },
                            React.createElement("i", { className: "fas fa-play mb-2 fa-lg" }),
                            React.createElement("h4", null, "PLAY")))))));
    }
}
exports.ServerTile = ServerTile;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2VydmVyVGlsZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy9qcy9jb21wb25lbnRzL3NlcnZlcnMvU2VydmVyVGlsZS50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBRS9CLE1BQU0sTUFBTSxHQUFHLE9BQU8sQ0FBQyw0QkFBNEIsQ0FBQyxDQUFDO0FBV3JELE1BQWEsVUFBVyxTQUFRLEtBQUssQ0FBQyxTQUF1QjtJQUl6RCxZQUFZLEtBQVk7UUFDcEIsS0FBSyxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ2IsSUFBSSxDQUFDLEtBQUssR0FBRyxFQUFFLGFBQWEsRUFBRSxDQUFDLEVBQUUsQ0FBQztJQUN0QyxDQUFDO0lBRUQsaUJBQWlCO1FBQ2IsSUFBSSxDQUFDLFVBQVUsR0FBRyxXQUFXLENBQUMsR0FBRyxFQUFFLENBQUMsSUFBSSxDQUFDLElBQUksRUFBRSxFQUFFLEVBQUUsR0FBRyxJQUFJLENBQUMsQ0FBQyxDQUFDLGVBQWU7UUFDNUUsSUFBSSxDQUFDLElBQUksRUFBRSxDQUFDLENBQUMsdUNBQXVDO0lBQ3hELENBQUM7SUFFRCxvQkFBb0I7UUFDaEIsYUFBYSxDQUFDLElBQUksQ0FBQyxVQUFVLENBQUMsQ0FBQztJQUNuQyxDQUFDO0lBRUQsSUFBSTtRQUNBLE1BQU0sQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsTUFBTSxFQUFFLEtBQUssRUFBRSxDQUFDLEtBQVUsRUFBRSxNQUFXLEVBQUUsRUFBRTtZQUNyRSxJQUFJLE9BQU8sR0FBRyxDQUFDLENBQUM7WUFFaEIsSUFBSSxLQUFLLEVBQUU7Z0JBQ1AsT0FBTyxDQUFDLEdBQUcsQ0FBQyxpQkFBaUIsR0FBRyxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sQ0FBQyxNQUFNLEdBQUcsSUFBSSxHQUFHLEtBQUssQ0FBQyxDQUFDO2FBQzVFO2lCQUFNO2dCQUNILE9BQU8sR0FBRyxNQUFNLENBQUMsT0FBTyxDQUFDLE1BQU0sQ0FBQzthQUNuQztZQUVELElBQUksQ0FBQyxRQUFRLENBQUMsRUFBRSxhQUFhLEVBQUUsT0FBTyxFQUFFLENBQUMsQ0FBQztRQUM5QyxDQUFDLENBQUMsQ0FBQztJQUNQLENBQUM7SUFFRCxNQUFNO1FBQ0YsT0FBTyxDQUFDLDZCQUFLLFNBQVMsRUFBQyxPQUFPO1lBQzFCLDZCQUFLLFNBQVMsRUFBQywwQkFBMEI7Z0JBQ3JDLDZCQUFLLFNBQVMsRUFBQyw0QkFBNEIsRUFBQyxLQUFLLEVBQUUsRUFBRSxlQUFlLEVBQUUsT0FBTyxHQUFHLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLFVBQVUsR0FBRyxJQUFJLEVBQUU7b0JBQ2pILDZCQUFLLFNBQVMsRUFBQyxjQUFjO3dCQUN6Qiw2QkFBSyxTQUFTLEVBQUMsYUFBYTs0QkFDeEIsNkJBQUssU0FBUyxFQUFFLEtBQUssRUFBRSxHQUFHLEVBQUUsSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsSUFBSSxFQUFFLEdBQUcsRUFBQyxNQUFNLEdBQUcsQ0FDL0Q7d0JBQ04sNkJBQUssU0FBUyxFQUFDLCtCQUErQjs0QkFDMUMsNEJBQUksU0FBUyxFQUFDLFlBQVksSUFBRSxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sQ0FBQyxJQUFJLENBQU07NEJBQ3hELDJCQUFHLFNBQVMsRUFBQyxXQUFXOztnQ0FBaUIsa0NBQU8sSUFBSSxDQUFDLEtBQUssQ0FBQyxhQUFhLENBQUMsY0FBYyxFQUFFLENBQVEsQ0FBSTs0QkFDckcsMkJBQUcsU0FBUyxFQUFDLFdBQVc7O2dDQUFLLGtDQUFPLElBQUksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLEVBQUUsQ0FBUSxDQUFJOzRCQUNwRSwyQkFBRyxTQUFTLEVBQUMsV0FBVzs7Z0NBQVMsa0NBQU8sSUFBSSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsTUFBTSxDQUFRLENBQUk7NEJBQzVFLDJCQUFHLFNBQVMsRUFBQyxXQUFXOztnQ0FBUSxrQ0FBTyxJQUFJLENBQUMsS0FBSyxDQUFDLE1BQU0sQ0FBQyxRQUFRLENBQVEsQ0FBSSxDQUMzRTt3QkFDTiw2QkFBSyxTQUFTLEVBQUMsa0NBQWtDLEVBQUMsT0FBTyxFQUFFLEdBQUcsRUFBRSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsT0FBTyxFQUFFOzRCQUNqRiwyQkFBRyxTQUFTLEVBQUMsd0JBQXdCLEdBQUc7NEJBQ3hDLHVDQUFhLENBQ1gsQ0FDSixDQUNKLENBQ0osQ0FDSixDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUF6REQsZ0NBeURDIn0=