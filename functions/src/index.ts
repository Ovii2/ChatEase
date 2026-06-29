import { setGlobalOptions } from "firebase-functions";
import {
  FirestoreEvent,
  QueryDocumentSnapshot,
  onDocumentCreated,
} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import { initializeApp } from "firebase-admin/app";
import { getFirestore } from "firebase-admin/firestore";
import { getMessaging } from "firebase-admin/messaging";

setGlobalOptions({ maxInstances: 10 });

initializeApp();

export const onContactRequestCreated = onDocumentCreated(
  "contact_requests/{requestId}",
  async (
    event: FirestoreEvent<QueryDocumentSnapshot | undefined, { requestId: string }>
  ): Promise<void> => {
    const request = event.data?.data();

    if (!request) {
      logger.warn("No contact request data");
      return;
    }

    const senderUserId = request.senderUserId;
    const receiverUserId = request.receiverUserId;

    if (!senderUserId || !receiverUserId) {
      logger.warn("Missing senderUserId or receiverUserId", { request });
      return;
    }

    const db = getFirestore();

    const [senderSnapshot, receiverSnapshot] = await Promise.all([
      db.collection("users").doc(senderUserId).get(),
      db.collection("users").doc(receiverUserId).get(),
    ]);

    const sender = senderSnapshot.data();
    const receiver = receiverSnapshot.data();

    const fcmToken = receiver?.fcmToken;

    if (!fcmToken) {
      logger.warn("Receiver has no FCM token", { receiverUserId });
      return;
    }

    const senderName = sender?.fullName?.trim().split(/\s+/)[0] ?? "";

    await getMessaging().send({
      token: fcmToken,
      data: {
        type: "contact_request",
        senderName,
        senderAvatar: sender?.imageUrl ?? "",
        senderUserId,
        receiverUserId,
        requestId: event.params.requestId,
      },
    });

    logger.info("Contact request notification sent", {
      requestId: event.params.requestId,
      senderUserId,
      receiverUserId,
    });
  }
);