let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendFollowingNotification = functions.database.ref('/users/{userId}/subscribers/{subscribeId}').onCreate((snapshot, context) => {

	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);

    console.log("snapshot: ", snapshot);
	//get the user id of the person who sent the message
	const senderId = snapshot.child('user id').val();
	console.log("senderId: ", senderId);

	//query the users node and get the name of the user who sent the message
	return admin.database().ref("/users/" + senderId).once('value').then(snap => {
		const senderName = snap.child("name").val();
		console.log("senderName: ", senderName);

		//get the token of the user receiving the message
		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
			const token = snap.child("token").val();
			console.log("token: ", token);

            if (token !== "-") {
                //we have everything we need
                //Build the message payload and send the message
                console.log("Construction the notification message.");
                const payload = {
                    data: {
                        data_type: "following",
                        user_id: senderId,
                        name: senderName,
                    }
                };

                return admin.messaging().sendToDevice(token, payload)
                            .then(function(response) {
                                console.log("Successfully sent message:", response);
                                return;
                              })
                              .catch(function(error) {
                                console.log("Error sending message:", error);
                              });
            } else {
                return;
            }
		});
	});
});

exports.sendOrderNotification = functions
    .database
    .ref('/users/{userId}/services/{serviceId}/working days/{wdId}/working time/{wtId}/orders/{orderId}')
    .onCreate((snapshot, context) => {

	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);

	//get the user id of the person who sent the message
	const userId = snapshot.child('user id').val();
	console.log("userId: ", userId);

	const serviceId = context.params.serviceId;
	const wdId = context.params.wdId;
	const wtId = context.params.wtId;

	//query the users node and get the name of the user who sent the message
	return admin.database().ref("/users/" + userId).once('value').then(snap => {
		const userName = snap.child("name").val();
		console.log("userName: ", userName);

            //get the token of the user receiving the message
        return admin.database().ref("/users/" + receiverId).once('value').then(snap => {

            const serviceSnap = snap.child("services").child(serviceId);
            const serviceName = serviceSnap.child("name").val();
            console.log("serviceName: ", serviceName);

            const wdSnap = serviceSnap.child("working days").child(wdId);
            const date = wdSnap.child("date").val();
            console.log("date: ", date);

            const time = wdSnap.child("working time").child(wtId).child("time").val();
            console.log("time: ", time);

            const token = snap.child("token").val();
            console.log("token: ", token);

            if (token !== "-") {
                //we have everything we need
                //Build the message payload and send the message
                console.log("Construction the notification message.");
                const payload = {
                    data: {
                        data_type: "order",
                        name: userName,
                        service_name: serviceName,
                        service_id: serviceId,
                        date: date,
                        time: time,
                    }
                };

                return admin.messaging().sendToDevice(token, payload)
                            .then(function(response) {
                                console.log("Successfully sent message:", response);
                                return;
                              })
                              .catch(function(error) {
                                console.log("Error sending message:", error);
                              });
            } else {
                return;
            }
        });
	});
});


exports.sendCancelNotification = functions
    .database
    .ref('/users/{userId}/services/{serviceId}/working days/{wdId}/working time/{wtId}/orders/{orderId}/is canceled')
    .onUpdate((change) => {

	//get the userId of the person receiving the notification because we need to get their token
	const workerId = context.params.userId;
	console.log("workerId: ", workerId);

	const serviceId = context.params.serviceId;
	const wdId = context.params.wdId;
	const wtId = context.params.wtId;
	const orderId = context.params.orderId;

	//query the users node and get the name of the user who sent the message
	return admin
	    .database()
	    .ref("/users/" + workerId + "/services/" + serviceId + "/working days/" + wdId + "/working time/" + wtId + "/orders/" + orderId)
    .once('value').then(snap => {

        const userId = snap.child('user id').val();
    	console.log("userId: ", userId);

        return admin.database().ref("/users/" + userId).once('value').then(snap => {

            const token = snap.child("token").val();
            console.log("token: ", token);

            if (token !== "-") {

                return admin.database().ref("/users/" + workerId).once('value').then(snap => {

                    const workerName = snap.child("name").val();
                    console.log("workerName: ", workerName);

                    const serviceSnap = snap.child("services").child(serviceId);
                    const serviceName = serviceSnap.child("name").val();
                    console.log("serviceName: ", serviceName);

                    const wdSnap = serviceSnap.child("working days").child(wdId);
                    const date = wdSnap.child("date").val();
                    console.log("date: ", date);

                    const time = wdSnap.child("working time").child(wtId).child("time").val();
                    console.log("time: ", time);

                    //we have everything we need
                    //Build the message payload and send the message
                    console.log("Construction the notification message.");
                    const payload = {
                        data: {
                            data_type: "cancel",
                            name: workerName,
                            service_name: serviceName,
                            service_id: serviceId,
                            date: date,
                            time: time,
                        }
                    };

                    return admin.messaging().sendToDevice(token, payload)
                                .then(function(response) {
                                    console.log("Successfully sent message:", response);
                                    return;
                                  })
                                  .catch(function(error) {
                                    console.log("Error sending message:", error);
                                  });
                });
            } else {
                return;
            }
        });
	});
});

exports.sendRatedServiceNotification = functions
    .database
    .ref('/users/{userId}/services/{serviceId}/working days/{wdId}/working time/{wtId}/orders/{orderId}/reviews/{reviewId}/rating')
    .onUpdate((change, context) => {

	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);

	const serviceId = context.params.serviceId;
    const wdId = context.params.wdId;
    const wtId = context.params.wtId;
    const orderId = context.params.orderId;

    //query the users node and get the name of the user who sent the message
    return admin
        .database()
        .ref("/users/" + receiverId + "/services/" + serviceId + "/working days/" + wdId + "/working time/" + wtId + "/orders/" + orderId)
        .once('value').then(snap => {

        const userId = snap.child('user id').val();
        console.log("userId: ", userId);

        return admin.database().ref("/users/" + userId).once('value').then(snap => {

            const userName = snap.child("name").val();
            console.log("userName: ", userName);

            //get the token of the user receiving the message
            return admin.database().ref("/users/" + receiverId).once('value').then(snap => {

                const serviceSnap = snap.child("services").child(serviceId);
                const serviceName = serviceSnap.child("name").val();
                console.log("serviceName: ", serviceName);

                const token = snap.child("token").val();
                console.log("token: ", token);

                if (token !== "-") {
                    //we have everything we need
                    //Build the message payload and send the message
                    console.log("Construction the notification message.");
                    const payload = {
                        data: {
                            data_type: "service rated",
                            name: userName,
                            service_name: serviceName,
                            service_id: serviceId,
                        }
                    };

                    return admin.messaging().sendToDevice(token, payload)
                                .then(function(response) {
                                    console.log("Successfully sent message:", response);
                                    return;
                                  })
                                  .catch(function(error) {
                                    console.log("Error sending message:", error);
                                  });
                } else {
                    return;
                }
            });
        });
    });
});

exports.sendRatedUserNotification = functions
    .database
    .ref('/users/{userId}/orders/{orderId}/reviews/{reviewId}/rating')
    .onUpdate((change, context) => {

	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);

	const orderId = context.params.orderId;
    console.log("orderId: ", orderId);

	//query the users node and get the name of the user who sent the message
	return admin.database().ref("/users/" + receiverId + "/orders/" + orderId + "/worker id").once('value').then(snap => {
		const workerId = snap.val();
		console.log("workerId: ", workerId);

        return admin.database().ref("/users/" + workerId + "/name").once('value').then(snap => {
                const workerName = snap.val();
                console.log("workerName: ", workerName);

            //get the token of the user receiving the message
            return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
                const token = snap.child("token").val();
                console.log("token: ", token);

                if (token !== "-") {
                    //we have everything we need
                    //Build the message payload and send the message
                    console.log("Construction the notification message.");
                    const payload = {
                        data: {
                            data_type: "user rated",
                            name: workerName,
                        }
                    };

                    return admin.messaging().sendToDevice(token, payload)
                                .then(function(response) {
                                    console.log("Successfully sent message:", response);
                                    return;
                                  })
                                  .catch(function(error) {
                                    console.log("Error sending message:", error);
                                  });
                } else {
                    return;
                }
            });
        });
	});
});
