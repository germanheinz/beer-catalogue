export type Role = "ADMIN" | "MANUFACTURER";

export type BeerType =
  | "IPA"
  | "LAGER"
  | "STOUT"
  | "ALE"
  | "PILSNER"
  | "PORTER"
  | "WHEAT"
  | "SOUR";

export interface AuthUser {
  id: number;
  username: string;
  role: Role;
  manufacturerId: number | null;
}

export interface ManufacturerResponse {
  id: number;
  name: string;
  country: string;
}

export interface BeerResponse {
  id: number;
  name: string;
  abv: number;
  type: BeerType;
  description: string;
  manufacturer: ManufacturerResponse;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface BeerFilter {
  name?: string;
  type?: BeerType;
  minAbv?: number;
  maxAbv?: number;
  manufacturerId?: number;
}
