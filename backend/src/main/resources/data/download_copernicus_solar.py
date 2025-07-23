import xarray as xr
import json

print("🔁 Opening GRIB file...")
ds = xr.open_dataset("copernicus_solar_data.grib", engine="cfgrib")

# Trim time range to 12 months
ds = ds.sel(time=slice("2023-01-01", "2023-12-31"))

# Reduce spatial resolution (lat/lon steps)
lats = ds.latitude.values[::20]
lons = ds.longitude.values[::20]
times = ds.time.values

print(f"📏 Sampling: {len(lats)} lat × {len(lons)} lon × {len(times)} months")

result = {}
counter = 0

for lat in lats:
    for lon in lons:
        try:
            values = ds['ssrd'].sel(latitude=lat, longitude=lon, method='nearest').values
            kwh = [round(float(v)/3.6e6, 2) for v in values]
            result[f"{round(lat,2)},{round(lon,2)}"] = {
                f"month_{i+1}": val for i, val in enumerate(kwh)
            }
            counter += 1
            if counter % 100 == 0:
                print(f"✅ Processed {counter} points...")
        except Exception as e:
            print(f"⚠️ Skipped point {lat},{lon} due to error: {e}")

print("💾 Writing output...")
with open("copernicus_solar_data.json", "w") as f:
    json.dump(result, f, indent=2)

print("✅ Done! JSON saved.")
