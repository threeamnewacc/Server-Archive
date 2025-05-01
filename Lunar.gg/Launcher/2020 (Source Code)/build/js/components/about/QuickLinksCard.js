"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.QuickLinksCard = void 0;
const React = require("react");
const QuickLink_1 = require("./QuickLink");
const constants_1 = require("../../constants");
class QuickLinksCard extends React.Component {
    render() {
        return (React.createElement("div", { className: "col-12 card" },
            React.createElement("h1", { className: "mb-3" },
                React.createElement("i", { className: "fas fa-external-link-alt mr-1" }),
                "QUICK LINKS"),
            React.createElement("div", { id: "quick-links" },
                React.createElement(QuickLink_1.QuickLink, { name: "Support", icon: "fa-ticket-alt", link: constants_1.SUPPORT_URL }),
                React.createElement(QuickLink_1.QuickLink, { name: "FAQ", icon: "fa-question", link: constants_1.FAQ_URL }),
                React.createElement(QuickLink_1.QuickLink, { name: "Website", icon: "fa-globe-americas", link: constants_1.WEBSITE_URL }))));
    }
}
exports.QuickLinksCard = QuickLinksCard;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUXVpY2tMaW5rc0NhcmQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvanMvY29tcG9uZW50cy9hYm91dC9RdWlja0xpbmtzQ2FyZC50c3giXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7O0FBQUEsK0JBQStCO0FBQy9CLDJDQUF3QztBQUN4QywrQ0FBb0U7QUFFcEUsTUFBYSxjQUFlLFNBQVEsS0FBSyxDQUFDLFNBQWlCO0lBRXZELE1BQU07UUFDRixPQUFPLENBQUMsNkJBQUssU0FBUyxFQUFDLGFBQWE7WUFDaEMsNEJBQUksU0FBUyxFQUFDLE1BQU07Z0JBQ2hCLDJCQUFHLFNBQVMsRUFBQywrQkFBK0IsR0FBRzs4QkFFOUM7WUFDTCw2QkFBSyxFQUFFLEVBQUMsYUFBYTtnQkFDakIsb0JBQUMscUJBQVMsSUFBQyxJQUFJLEVBQUMsU0FBUyxFQUFDLElBQUksRUFBQyxlQUFlLEVBQUMsSUFBSSxFQUFFLHVCQUFXLEdBQUk7Z0JBQ3BFLG9CQUFDLHFCQUFTLElBQUMsSUFBSSxFQUFDLEtBQUssRUFBQyxJQUFJLEVBQUMsYUFBYSxFQUFDLElBQUksRUFBRSxtQkFBTyxHQUFJO2dCQUMxRCxvQkFBQyxxQkFBUyxJQUFDLElBQUksRUFBQyxTQUFTLEVBQUMsSUFBSSxFQUFDLG1CQUFtQixFQUFDLElBQUksRUFBRSx1QkFBVyxHQUFJLENBQ3RFLENBQ0osQ0FBQyxDQUFDO0lBQ1osQ0FBQztDQUVKO0FBaEJELHdDQWdCQyJ9