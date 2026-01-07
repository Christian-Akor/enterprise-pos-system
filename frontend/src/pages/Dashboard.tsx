export default function Dashboard() {
  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
      <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="text-sm font-medium text-gray-500">Total Sales</div>
            <div className="mt-1 text-3xl font-semibold text-gray-900">$0</div>
          </div>
        </div>
      </div>
    </div>
  );
}