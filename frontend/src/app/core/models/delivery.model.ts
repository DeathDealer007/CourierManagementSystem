export interface Parcel {
  id?: number;
  weight: number;
  dimensions: string;
  fragile: boolean;
}

export interface Address {
  id?: number;
  type: 'SENDER' | 'RECEIVER';
  name: string;
  phone: string;
  address: string;
  city: string;
}

export interface Delivery {
  id?: number;
  trackingNumber: string;
  userId: number;
  serviceType: 'STANDARD' | 'EXPRESS' | 'OVERNIGHT' | 'SAME_DAY';
  status: 'CREATED' | 'PICKED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'FAILED' | 'CANCELLED';
  expectedDeliveryDate?: string;
  createdAt?: string;
  deliveredAt?: string;
  totalAmount: number;
  parcel: Parcel;
  addresses: Address[];
}
