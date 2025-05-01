"use strict";
const net = require('net');
const dns = require('dns');
const { PacketDecoder, createHandshakePacket, createPingPacket } = require('minecraft-pinger/lib/packet');
const ping = module.exports.ping = (hostname, port, callback) => {
    checkSrvRecord(hostname)
        .then(openConnection, _ => openConnection({ hostname, port }))
        .then(data => callback(null, data))
        .catch(callback);
};
module.exports.pingPromise = (hostname, port) => {
    return new Promise((resolve, reject) => {
        ping(hostname, port, (error, result) => {
            error ? reject(error) : resolve(result);
        });
    });
};
function openConnection(address) {
    const { hostname, port } = address;
    return new Promise((resolve, reject) => {
        let connection = net.createConnection(port, hostname, () => {
            // Decode incoming packets
            let packetDecoder = new PacketDecoder();
            connection.pipe(packetDecoder);
            // Write handshake packet
            connection.write(createHandshakePacket(hostname, port));
            packetDecoder.once('error', error => {
                connection.destroy();
                clearTimeout(timeout);
                reject(error);
            });
            packetDecoder.once('packet', data => {
                // Write ping packet
                connection.write(createPingPacket(Date.now()));
                packetDecoder.once('packet', ping => {
                    connection.end();
                    clearTimeout(timeout);
                    data.ping = ping;
                    resolve(data);
                });
            });
        });
        // Destroy on error
        connection.once('error', error => {
            connection.destroy();
            clearTimeout(timeout);
            reject(error);
        });
        // Destroy on timeout
        connection.once('timeout', () => {
            connection.destroy();
            clearTimeout(timeout);
            reject(new Error('Timed out'));
        });
        // Packet timeout (10 seconds)
        let timeout = setTimeout(() => {
            connection.end();
            reject(new Error('Timed out (10 seconds passed)'));
        }, 10000);
    });
}
function checkSrvRecord(hostname) {
    return new Promise((resolve, reject) => {
        if (net.isIP(hostname) !== 0) {
            reject(new Error('Hostname is an IP address'));
        }
        else {
            dns.resolveSrv('_minecraft._tcp.' + hostname, (error, result) => {
                // MODIFIED - treat an empty array as a fail
                if (result !== undefined && Object.keys(result).length === 0) {
                    reject(null);
                    return;
                }
                error ? reject(error) : resolve({
                    hostname: result[0].name,
                    port: result[0].port
                });
            });
        }
    });
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWluZWNyYWZ0LXBpbmdlci5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uL3NyYy9qcy9saWIvbWluZWNyYWZ0LXBpbmdlci5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO0FBQUEsTUFBTSxHQUFHLEdBQUcsT0FBTyxDQUFDLEtBQUssQ0FBQyxDQUFBO0FBQzFCLE1BQU0sR0FBRyxHQUFHLE9BQU8sQ0FBQyxLQUFLLENBQUMsQ0FBQTtBQUMxQixNQUFNLEVBQ0YsYUFBYSxFQUNiLHFCQUFxQixFQUNyQixnQkFBZ0IsRUFDbkIsR0FBRyxPQUFPLENBQUMsNkJBQTZCLENBQUMsQ0FBQTtBQUUxQyxNQUFNLElBQUksR0FBRyxNQUFNLENBQUMsT0FBTyxDQUFDLElBQUksR0FBRyxDQUFDLFFBQVEsRUFBRSxJQUFJLEVBQUUsUUFBUSxFQUFFLEVBQUU7SUFDNUQsY0FBYyxDQUFDLFFBQVEsQ0FBQztTQUNuQixJQUFJLENBQUMsY0FBYyxFQUFFLENBQUMsQ0FBQyxFQUFFLENBQUMsY0FBYyxDQUFDLEVBQUUsUUFBUSxFQUFFLElBQUksRUFBRSxDQUFDLENBQUM7U0FDN0QsSUFBSSxDQUFDLElBQUksQ0FBQyxFQUFFLENBQUMsUUFBUSxDQUFDLElBQUksRUFBRSxJQUFJLENBQUMsQ0FBQztTQUNsQyxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUE7QUFDeEIsQ0FBQyxDQUFBO0FBRUQsTUFBTSxDQUFDLE9BQU8sQ0FBQyxXQUFXLEdBQUcsQ0FBQyxRQUFRLEVBQUUsSUFBSSxFQUFFLEVBQUU7SUFDNUMsT0FBTyxJQUFJLE9BQU8sQ0FBQyxDQUFDLE9BQU8sRUFBRSxNQUFNLEVBQUUsRUFBRTtRQUNuQyxJQUFJLENBQUMsUUFBUSxFQUFFLElBQUksRUFBRSxDQUFDLEtBQUssRUFBRSxNQUFNLEVBQUUsRUFBRTtZQUNuQyxLQUFLLENBQUMsQ0FBQyxDQUFDLE1BQU0sQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLENBQUMsT0FBTyxDQUFDLE1BQU0s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