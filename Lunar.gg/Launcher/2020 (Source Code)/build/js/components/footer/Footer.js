"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Footer = void 0;
const React = require("react");
const constants_1 = require("../../constants");
const FooterLink_1 = require("./FooterLink");
class Footer extends React.Component {
    render() {
        return (React.createElement("div", { id: "footer" },
            React.createElement("footer", { id: "footer" },
                React.createElement("div", { className: "container-fluid footer-contents" },
                    React.createElement("div", { id: "footer-inner", className: "row" },
                        React.createElement("div", { id: "footer-left", className: "col" },
                            React.createElement("img", { draggable: false, src: "images/moonsworth.png", className: "mr-1", alt: "Moonsworth Logo" }),
                            "\u00A9 Moonsworth, LLC ",
                            new Date().getFullYear(),
                            " \u2022 v",
                            constants_1.VERSION),
                        React.createElement("div", { id: "footer-middle", className: "col-4" },
                            React.createElement(FooterLink_1.FooterLink, { icon: "fab fa-telegram", link: constants_1.TELEGRAM_URL }),
                            React.createElement(FooterLink_1.FooterLink, { icon: "fab fa-twitter", link: constants_1.TWITTER_URL }),
                            React.createElement(FooterLink_1.FooterLink, { icon: "fab fa-discord", link: constants_1.DISCORD_URL }),
                            React.createElement(FooterLink_1.FooterLink, { icon: "fas fa-globe-americas", link: constants_1.WEBSITE_URL })),
                        React.createElement("div", { id: "footer-right", className: "col" }, "Not affiliated with Mojang, AB."))))));
    }
}
exports.Footer = Footer;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiRm9vdGVyLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vLi4vLi4vc3JjL2pzL2NvbXBvbmVudHMvZm9vdGVyL0Zvb3Rlci50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLCtDQUErRjtBQUMvRiw2Q0FBMEM7QUFFMUMsTUFBYSxNQUFPLFNBQVEsS0FBSyxDQUFDLFNBQWlCO0lBRS9DLE1BQU07UUFDRixPQUFPLENBQUMsNkJBQUssRUFBRSxFQUFDLFFBQVE7WUFDcEIsZ0NBQVEsRUFBRSxFQUFDLFFBQVE7Z0JBQ2YsNkJBQUssU0FBUyxFQUFDLGlDQUFpQztvQkFDNUMsNkJBQUssRUFBRSxFQUFDLGNBQWMsRUFBQyxTQUFTLEVBQUMsS0FBSzt3QkFDbEMsNkJBQUssRUFBRSxFQUFDLGFBQWEsRUFBQyxTQUFTLEVBQUMsS0FBSzs0QkFDakMsNkJBQUssU0FBUyxFQUFFLEtBQUssRUFBRSxHQUFHLEVBQUMsdUJBQXVCLEVBQUMsU0FBUyxFQUFDLE1BQU0sRUFBQyxHQUFHLEVBQUMsaUJBQWlCLEdBQUc7OzRCQUNwRSxJQUFJLElBQUksRUFBRSxDQUFDLFdBQVcsRUFBRTs7NEJBQVcsbUJBQU8sQ0FDaEU7d0JBQ04sNkJBQUssRUFBRSxFQUFDLGVBQWUsRUFBQyxTQUFTLEVBQUMsT0FBTzs0QkFDckMsb0JBQUMsdUJBQVUsSUFBQyxJQUFJLEVBQUMsaUJBQWlCLEVBQUMsSUFBSSxFQUFFLHdCQUFZLEdBQUk7NEJBQ3pELG9CQUFDLHVCQUFVLElBQUMsSUFBSSxFQUFDLGdCQUFnQixFQUFDLElBQUksRUFBRSx1QkFBVyxHQUFJOzRCQUN2RCxvQkFBQyx1QkFBVSxJQUFDLElBQUksRUFBQyxnQkFBZ0IsRUFBQyxJQUFJLEVBQUUsdUJBQVcsR0FBSTs0QkFDdkQsb0JBQUMsdUJBQVUsSUFBQyxJQUFJLEVBQUMsdUJBQXVCLEVBQUMsSUFBSSxFQUFFLHVCQUFXLEdBQUksQ0FDNUQ7d0JBQ04sNkJBQUssRUFBRSxFQUFDLGNBQWMsRUFBQyxTQUFTLEVBQUMsS0FBSyxzQ0FFaEMsQ0FDSixDQUNKLENBQ0QsQ0FDUCxDQUFDLENBQUM7SUFDWixDQUFDO0NBRUo7QUExQkQsd0JBMEJDIn0=