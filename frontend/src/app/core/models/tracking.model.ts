export interface Tracking {
  id?: number;
  trackingNumber: string;
  status: string;
  location: string;
  timestamp: string;
  description: string;
}

export interface DeliveryProof {
  id?: number;
  deliveryId: number;
  proofData: string;
  timestamp: string;
}

export interface Document {
  id?: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  filePath: string;
  deliveryId: number;
  userId: number;
}
